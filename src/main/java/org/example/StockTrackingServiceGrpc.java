package org.example;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service definition
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: StockTrackingService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class StockTrackingServiceGrpc {

  private StockTrackingServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "StockTrackingService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.example.StockQueryRequest,
      org.example.StockStatus> getQueryStockMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QueryStock",
      requestType = org.example.StockQueryRequest.class,
      responseType = org.example.StockStatus.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.example.StockQueryRequest,
      org.example.StockStatus> getQueryStockMethod() {
    io.grpc.MethodDescriptor<org.example.StockQueryRequest, org.example.StockStatus> getQueryStockMethod;
    if ((getQueryStockMethod = StockTrackingServiceGrpc.getQueryStockMethod) == null) {
      synchronized (StockTrackingServiceGrpc.class) {
        if ((getQueryStockMethod = StockTrackingServiceGrpc.getQueryStockMethod) == null) {
          StockTrackingServiceGrpc.getQueryStockMethod = getQueryStockMethod =
              io.grpc.MethodDescriptor.<org.example.StockQueryRequest, org.example.StockStatus>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryStock"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.example.StockQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.example.StockStatus.getDefaultInstance()))
              .setSchemaDescriptor(new StockTrackingServiceMethodDescriptorSupplier("QueryStock"))
              .build();
        }
      }
    }
    return getQueryStockMethod;
  }

  private static volatile io.grpc.MethodDescriptor<org.example.StockQueryRequest,
      org.example.StockStatus> getSubscribeStockUpdatesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubscribeStockUpdates",
      requestType = org.example.StockQueryRequest.class,
      responseType = org.example.StockStatus.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<org.example.StockQueryRequest,
      org.example.StockStatus> getSubscribeStockUpdatesMethod() {
    io.grpc.MethodDescriptor<org.example.StockQueryRequest, org.example.StockStatus> getSubscribeStockUpdatesMethod;
    if ((getSubscribeStockUpdatesMethod = StockTrackingServiceGrpc.getSubscribeStockUpdatesMethod) == null) {
      synchronized (StockTrackingServiceGrpc.class) {
        if ((getSubscribeStockUpdatesMethod = StockTrackingServiceGrpc.getSubscribeStockUpdatesMethod) == null) {
          StockTrackingServiceGrpc.getSubscribeStockUpdatesMethod = getSubscribeStockUpdatesMethod =
              io.grpc.MethodDescriptor.<org.example.StockQueryRequest, org.example.StockStatus>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubscribeStockUpdates"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.example.StockQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.example.StockStatus.getDefaultInstance()))
              .setSchemaDescriptor(new StockTrackingServiceMethodDescriptorSupplier("SubscribeStockUpdates"))
              .build();
        }
      }
    }
    return getSubscribeStockUpdatesMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static StockTrackingServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StockTrackingServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StockTrackingServiceStub>() {
        @java.lang.Override
        public StockTrackingServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StockTrackingServiceStub(channel, callOptions);
        }
      };
    return StockTrackingServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static StockTrackingServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StockTrackingServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StockTrackingServiceBlockingStub>() {
        @java.lang.Override
        public StockTrackingServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StockTrackingServiceBlockingStub(channel, callOptions);
        }
      };
    return StockTrackingServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static StockTrackingServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<StockTrackingServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<StockTrackingServiceFutureStub>() {
        @java.lang.Override
        public StockTrackingServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new StockTrackingServiceFutureStub(channel, callOptions);
        }
      };
    return StockTrackingServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service definition
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * simple RPC: Check the inventory status of a specific item
     * </pre>
     */
    default void queryStock(org.example.StockQueryRequest request,
        io.grpc.stub.StreamObserver<org.example.StockStatus> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryStockMethod(), responseObserver);
    }

    /**
     * <pre>
     * server-side streaming RPC: Send inventory status regularly
     * </pre>
     */
    default void subscribeStockUpdates(org.example.StockQueryRequest request,
        io.grpc.stub.StreamObserver<org.example.StockStatus> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubscribeStockUpdatesMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service StockTrackingService.
   * <pre>
   * Service definition
   * </pre>
   */
  public static abstract class StockTrackingServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return StockTrackingServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service StockTrackingService.
   * <pre>
   * Service definition
   * </pre>
   */
  public static final class StockTrackingServiceStub
      extends io.grpc.stub.AbstractAsyncStub<StockTrackingServiceStub> {
    private StockTrackingServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StockTrackingServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StockTrackingServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * simple RPC: Check the inventory status of a specific item
     * </pre>
     */
    public void queryStock(org.example.StockQueryRequest request,
        io.grpc.stub.StreamObserver<org.example.StockStatus> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryStockMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * server-side streaming RPC: Send inventory status regularly
     * </pre>
     */
    public void subscribeStockUpdates(org.example.StockQueryRequest request,
        io.grpc.stub.StreamObserver<org.example.StockStatus> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getSubscribeStockUpdatesMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service StockTrackingService.
   * <pre>
   * Service definition
   * </pre>
   */
  public static final class StockTrackingServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<StockTrackingServiceBlockingStub> {
    private StockTrackingServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StockTrackingServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StockTrackingServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * simple RPC: Check the inventory status of a specific item
     * </pre>
     */
    public org.example.StockStatus queryStock(org.example.StockQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getQueryStockMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * server-side streaming RPC: Send inventory status regularly
     * </pre>
     */
    public java.util.Iterator<org.example.StockStatus> subscribeStockUpdates(
        org.example.StockQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getSubscribeStockUpdatesMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service StockTrackingService.
   * <pre>
   * Service definition
   * </pre>
   */
  public static final class StockTrackingServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<StockTrackingServiceFutureStub> {
    private StockTrackingServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected StockTrackingServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new StockTrackingServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * simple RPC: Check the inventory status of a specific item
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<org.example.StockStatus> queryStock(
        org.example.StockQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryStockMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_QUERY_STOCK = 0;
  private static final int METHODID_SUBSCRIBE_STOCK_UPDATES = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_QUERY_STOCK:
          serviceImpl.queryStock((org.example.StockQueryRequest) request,
              (io.grpc.stub.StreamObserver<org.example.StockStatus>) responseObserver);
          break;
        case METHODID_SUBSCRIBE_STOCK_UPDATES:
          serviceImpl.subscribeStockUpdates((org.example.StockQueryRequest) request,
              (io.grpc.stub.StreamObserver<org.example.StockStatus>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getQueryStockMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              org.example.StockQueryRequest,
              org.example.StockStatus>(
                service, METHODID_QUERY_STOCK)))
        .addMethod(
          getSubscribeStockUpdatesMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              org.example.StockQueryRequest,
              org.example.StockStatus>(
                service, METHODID_SUBSCRIBE_STOCK_UPDATES)))
        .build();
  }

  private static abstract class StockTrackingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    StockTrackingServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.example.StockTrackingServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("StockTrackingService");
    }
  }

  private static final class StockTrackingServiceFileDescriptorSupplier
      extends StockTrackingServiceBaseDescriptorSupplier {
    StockTrackingServiceFileDescriptorSupplier() {}
  }

  private static final class StockTrackingServiceMethodDescriptorSupplier
      extends StockTrackingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    StockTrackingServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (StockTrackingServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new StockTrackingServiceFileDescriptorSupplier())
              .addMethod(getQueryStockMethod())
              .addMethod(getSubscribeStockUpdatesMethod())
              .build();
        }
      }
    }
    return result;
  }
}
