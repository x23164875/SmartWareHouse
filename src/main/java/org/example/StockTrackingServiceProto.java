// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: StockTrackingService.proto

// Protobuf Java Version: 3.25.1
package org.example;

public final class StockTrackingServiceProto {
  private StockTrackingServiceProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_StockItem_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_StockItem_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_StockQueryRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_StockQueryRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_StockStatus_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_StockStatus_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\032StockTrackingService.proto\"G\n\tStockIte" +
      "m\022\022\n\nproduct_id\030\001 \001(\005\022\024\n\014product_name\030\002 " +
      "\001(\t\022\020\n\010quantity\030\003 \001(\005\")\n\021StockQueryReque" +
      "st\022\024\n\014product_name\030\001 \001(\t\"a\n\013StockStatus\022" +
      "\022\n\nproduct_id\030\001 \001(\005\022\024\n\014product_name\030\002 \001(" +
      "\t\022\030\n\020current_quantity\030\003 \001(\005\022\016\n\006status\030\004 " +
      "\001(\t2\203\001\n\024StockTrackingService\022.\n\nQuerySto" +
      "ck\022\022.StockQueryRequest\032\014.StockStatus\022;\n\025" +
      "SubscribeStockUpdates\022\022.StockQueryReques" +
      "t\032\014.StockStatus0\001B*\n\013org.exampleB\031StockT" +
      "rackingServiceProtoP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_StockItem_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_StockItem_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_StockItem_descriptor,
        new java.lang.String[] { "ProductId", "ProductName", "Quantity", });
    internal_static_StockQueryRequest_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_StockQueryRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_StockQueryRequest_descriptor,
        new java.lang.String[] { "ProductName", });
    internal_static_StockStatus_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_StockStatus_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_StockStatus_descriptor,
        new java.lang.String[] { "ProductId", "ProductName", "CurrentQuantity", "Status", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}