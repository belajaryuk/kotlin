/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.configuration

import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.ProjectKeys
import com.intellij.openapi.externalSystem.model.project.ModuleData
import com.intellij.openapi.externalSystem.model.project.ProjectData
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider
import com.intellij.openapi.externalSystem.service.project.manage.AbstractProjectDataService
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.DependencyScope
import com.intellij.openapi.roots.ExportableOrderEntry
import com.intellij.openapi.roots.ModifiableRootModel
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.config.CoroutineSupport
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.KotlinModuleKind
import org.jetbrains.kotlin.gradle.KotlinCompilation
import org.jetbrains.kotlin.gradle.KotlinModule
import org.jetbrains.kotlin.gradle.KotlinPlatform
import org.jetbrains.kotlin.gradle.KotlinSourceSet
import org.jetbrains.kotlin.idea.facet.*
import org.jetbrains.kotlin.idea.inspections.gradle.findAll
import org.jetbrains.kotlin.idea.inspections.gradle.findKotlinPluginVersion
import org.jetbrains.kotlin.idea.platform.IdePlatformKindTooling
import org.jetbrains.kotlin.idea.roots.migrateNonJvmSourceFolders
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.platform.impl.JvmIdePlatformKind
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.plugins.gradle.model.data.BuildScriptClasspathData
import org.jetbrains.plugins.gradle.model.data.GradleSourceSetData

class KotlinSourceSetDataService : AbstractProjectDataService<GradleSourceSetData, Void>() {
    override fun getTargetDataKey() = GradleSourceSetData.KEY

    override fun postProcess(
        toImport: MutableCollection<DataNode<GradleSourceSetData>>,
        projectData: ProjectData?,
        project: Project,
        modelsProvider: IdeModifiableModelsProvider
    ) {
        for (nodeToImport in toImport) {
            val mainModuleData = ExternalSystemApiUtil.findParent(
                nodeToImport,
                ProjectKeys.MODULE
            ) ?: continue
            val sourceSetData = nodeToImport.data
            val kotlinSourceSet = nodeToImport.kotlinSourceSet ?: continue
            val ideModule = modelsProvider.findIdeModule(sourceSetData) ?: continue
            val platform = kotlinSourceSet.actualPlatforms
            val rootModel = modelsProvider.getModifiableRootModel(ideModule)

            if (platform.platforms.any { it != KotlinPlatform.JVM && it != KotlinPlatform.ANDROID }) {
                migrateNonJvmSourceFolders(rootModel)
            }

            configureFacet(sourceSetData, kotlinSourceSet, mainModuleData, ideModule, modelsProvider)?.let { facet ->
                GradleProjectImportHandler.getInstances(project).forEach { it.importBySourceSet(facet, nodeToImport) }
            }

            if (kotlinSourceSet.isTestModule) {
                assignTestScope(rootModel)
            }
        }
    }

    private fun assignTestScope(rootModel: ModifiableRootModel) {
        rootModel
            .orderEntries
            .asSequence()
            .filterIsInstance<ExportableOrderEntry>()
            .filter { it.scope == DependencyScope.COMPILE }
            .forEach { it.scope = DependencyScope.TEST }
    }

    companion object {
        private val KotlinModule.kind
            get() = when (this) {
                is KotlinCompilation -> KotlinModuleKind.COMPILATION_AND_SOURCE_SET_HOLDER
                is KotlinSourceSet -> KotlinModuleKind.SOURCE_SET_HOLDER
                else -> KotlinModuleKind.DEFAULT
            }

        fun configureFacet(
            moduleData: ModuleData,
            kotlinSourceSet: KotlinSourceSetInfo,
            mainModuleNode: DataNode<ModuleData>,
            ideModule: Module,
            modelsProvider: IdeModifiableModelsProvider
        ): KotlinFacet? {
            val compilerVersion = mainModuleNode
                .findAll(BuildScriptClasspathData.KEY)
                .firstOrNull()
                ?.data
                ?.let { findKotlinPluginVersion(it) }// ?: return null TODO: Fix in CLion or our plugin KT-27623

            val platformKinds = kotlinSourceSet.actualPlatforms.platforms //TODO(auskov): fix calculation of jvm target
                .map { IdePlatformKindTooling.getTooling(it).kind }
                .flatMap {
                    when (it) {
                        is JvmIdePlatformKind -> {
                            val target = JvmTarget.fromString(moduleData.targetCompatibility ?: "") ?: JvmTarget.DEFAULT
                            JvmPlatforms.jvmPlatformByTargetVersion(target).componentPlatforms
                        }
                        else -> it.defaultPlatform.componentPlatforms
                    }
                }
                .distinct()
                .toSet()

            val platform = TargetPlatform(platformKinds)

            val coroutinesProperty = CoroutineSupport.byCompilerArgument(
                mainModuleNode.coroutines ?: findKotlinCoroutinesProperty(ideModule.project)
            )

            val kotlinFacet = ideModule.getOrCreateFacet(modelsProvider, false)
            kotlinFacet.configureFacet(
                compilerVersion,
                coroutinesProperty,
                platform,
                modelsProvider,
                mainModuleNode.isHmpp,
                mainModuleNode.pureKotlinSourceFolders,
                kotlinSourceSet.dependsOn
            )

            val compilerArguments = kotlinSourceSet.compilerArguments
            val defaultCompilerArguments = kotlinSourceSet.defaultCompilerArguments
            if (compilerArguments != null) {
                applyCompilerArgumentsToFacet(
                    compilerArguments,
                    defaultCompilerArguments,
                    kotlinFacet,
                    modelsProvider
                )
            }

            adjustClasspath(kotlinFacet, kotlinSourceSet.dependencyClasspath)

            kotlinFacet.noVersionAutoAdvance()

            with(kotlinFacet.configuration.settings) {
                kind = kotlinSourceSet.kotlinModule.kind

                isTestModule = kotlinSourceSet.isTestModule
                externalSystemRunTasks = ArrayList(kotlinSourceSet.externalSystemRunTasks)

                externalProjectId = kotlinSourceSet.gradleModuleId

                sourceSetNames = kotlinSourceSet.sourceSetIdsByName.values.mapNotNull { sourceSetId ->
                    val node = mainModuleNode.findChildModuleById(sourceSetId) ?: return@mapNotNull null
                    val data = node.data as? ModuleData ?: return@mapNotNull null
                    modelsProvider.findIdeModule(data)?.name
                }

                if (kotlinSourceSet.isTestModule) {
                    testOutputPath = (kotlinSourceSet.compilerArguments as? K2JSCompilerArguments)?.outputFile
                    productionOutputPath = null
                } else {
                    productionOutputPath = (kotlinSourceSet.compilerArguments as? K2JSCompilerArguments)?.outputFile
                    testOutputPath = null
                }

                this.pureKotlinSourceFolders = mainModuleNode.pureKotlinSourceFolders
            }

            return kotlinFacet
        }
    }
}