// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: core/metadata/src/ext_options.debug.proto

package org.jetbrains.kotlin.serialization;

public final class DebugExtOptionsProtoBuf {
  private DebugExtOptionsProtoBuf() {}
  public static void registerAllExtensions(
      org.jetbrains.kotlin.protobuf.ExtensionRegistry registry) {
    registry.add(org.jetbrains.kotlin.serialization.DebugExtOptionsProtoBuf.skipInComparison);
    registry.add(org.jetbrains.kotlin.serialization.DebugExtOptionsProtoBuf.nameIdInTable);
    registry.add(org.jetbrains.kotlin.serialization.DebugExtOptionsProtoBuf.fqNameIdInTable);
    registry.add(org.jetbrains.kotlin.serialization.DebugExtOptionsProtoBuf.stringIdInTable);
  }
  public static final int SKIP_IN_COMPARISON_FIELD_NUMBER = 50000;
  /**
   * <code>extend .google.protobuf.FieldOptions { ... }</code>
   */
  public static final
    org.jetbrains.kotlin.protobuf.GeneratedMessage.GeneratedExtension<
      org.jetbrains.kotlin.protobuf.DescriptorProtos.FieldOptions,
      java.lang.Boolean> skipInComparison = org.jetbrains.kotlin.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.Boolean.class,
        null);
  public static final int NAME_ID_IN_TABLE_FIELD_NUMBER = 50001;
  /**
   * <code>extend .google.protobuf.FieldOptions { ... }</code>
   */
  public static final
    org.jetbrains.kotlin.protobuf.GeneratedMessage.GeneratedExtension<
      org.jetbrains.kotlin.protobuf.DescriptorProtos.FieldOptions,
      java.lang.Boolean> nameIdInTable = org.jetbrains.kotlin.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.Boolean.class,
        null);
  public static final int FQ_NAME_ID_IN_TABLE_FIELD_NUMBER = 50002;
  /**
   * <code>extend .google.protobuf.FieldOptions { ... }</code>
   */
  public static final
    org.jetbrains.kotlin.protobuf.GeneratedMessage.GeneratedExtension<
      org.jetbrains.kotlin.protobuf.DescriptorProtos.FieldOptions,
      java.lang.Boolean> fqNameIdInTable = org.jetbrains.kotlin.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.Boolean.class,
        null);
  public static final int STRING_ID_IN_TABLE_FIELD_NUMBER = 50003;
  /**
   * <code>extend .google.protobuf.FieldOptions { ... }</code>
   */
  public static final
    org.jetbrains.kotlin.protobuf.GeneratedMessage.GeneratedExtension<
      org.jetbrains.kotlin.protobuf.DescriptorProtos.FieldOptions,
      java.lang.Boolean> stringIdInTable = org.jetbrains.kotlin.protobuf.GeneratedMessage
          .newFileScopedGeneratedExtension(
        java.lang.Boolean.class,
        null);

  public static org.jetbrains.kotlin.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static org.jetbrains.kotlin.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n)core/metadata/src/ext_options.debug.pr" +
      "oto\022\"org.jetbrains.kotlin.serialization\032" +
      " google/protobuf/descriptor.proto:;\n\022ski" +
      "p_in_comparison\022\035.google.protobuf.FieldO" +
      "ptions\030\320\206\003 \001(\010:9\n\020name_id_in_table\022\035.goo" +
      "gle.protobuf.FieldOptions\030\321\206\003 \001(\010:<\n\023fq_" +
      "name_id_in_table\022\035.google.protobuf.Field" +
      "Options\030\322\206\003 \001(\010:;\n\022string_id_in_table\022\035." +
      "google.protobuf.FieldOptions\030\323\206\003 \001(\010B\031B\027" +
      "DebugExtOptionsProtoBuf"
    };
    org.jetbrains.kotlin.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new org.jetbrains.kotlin.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public org.jetbrains.kotlin.protobuf.ExtensionRegistry assignDescriptors(
              org.jetbrains.kotlin.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    org.jetbrains.kotlin.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new org.jetbrains.kotlin.protobuf.Descriptors.FileDescriptor[] {
          org.jetbrains.kotlin.protobuf.DescriptorProtos.getDescriptor(),
        }, assigner);
    skipInComparison.internalInit(descriptor.getExtensions().get(0));
    nameIdInTable.internalInit(descriptor.getExtensions().get(1));
    fqNameIdInTable.internalInit(descriptor.getExtensions().get(2));
    stringIdInTable.internalInit(descriptor.getExtensions().get(3));
    org.jetbrains.kotlin.protobuf.DescriptorProtos.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}