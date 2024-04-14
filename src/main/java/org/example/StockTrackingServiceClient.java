package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;


import java.util.concurrent.TimeUnit;

public class StockTrackingServiceClient {
    private final ManagedChannel channel;
    private final StockTrackingServiceGrpc.StockTrackingServiceBlockingStub blockingStub;
    private final StockTrackingServiceGrpc.StockTrackingServiceStub asyncStub;

    public StockTrackingServiceClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = StockTrackingServiceGrpc.newBlockingStub(channel);
        this.asyncStub = StockTrackingServiceGrpc.newStub(channel);
    }

    // simple RPC: Check the inventory status of a specific item
    public void queryStock(String productName) {
        StockQueryRequest request = StockQueryRequest.newBuilder()
                .setProductName(productName)
                .build();
        StockStatus response = blockingStub.queryStock(request);
        System.out.println("Received response: Product ID: " + response.getProductId()
                + ", Product Name: " + response.getProductName()
                + ", Quantity: " + response.getCurrentQuantity()
                + ", Status: " + response.getStatus());
    }

    // server-side streaming RPC: Send inventory status regularly
    public void subscribeStockUpdates(String productName) {
        StockQueryRequest request = StockQueryRequest.newBuilder()
                .setProductName(productName)
                .build();
        StreamObserver<StockStatus> responseObserver = new StreamObserver<StockStatus>() {
            @Override
            public void onNext(StockStatus value) {
                System.out.println("Stream response: Product ID: " + value.getProductId()
                        + ", Product Name: " + value.getProductName()
                        + ", Quantity: " + value.getCurrentQuantity()
                        + ", Status: " + value.getStatus());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error during SubscribeStockUpdates: " + t);
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed.");
            }
        };
        asyncStub.subscribeStockUpdates(request, responseObserver);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(12, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        StockTrackingServiceClient client = new StockTrackingServiceClient("localhost", 9090);
        client.queryStock("printer");
        client.subscribeStockUpdates("printer");
        client.shutdown();
    }
}
