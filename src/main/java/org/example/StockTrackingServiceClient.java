package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;
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
        System.out.println("Received response: \nProduct ID: " + response.getProductId()
                + "\nProduct Name: " + response.getProductName()
                + "\nQuantity: " + response.getCurrentQuantity()
                + "\nStatus: " + response.getStatus());
    }

    // server-side streaming RPC: Send inventory status regularly
    public void subscribeStockUpdates(String productName) {
        StockQueryRequest request = StockQueryRequest.newBuilder()
                .setProductName(productName)
                .build();
        StreamObserver<StockStatus> responseObserver = new StreamObserver<StockStatus>() {
            @Override
            public void onNext(StockStatus value) {
                System.out.println("Response: \nProduct ID: " + value.getProductId()
                        + "\nProduct Name: " + value.getProductName()
                        + "\nQuantity: " + value.getCurrentQuantity()
                        + "\nStatus: " + value.getStatus());
                System.out.println("-------------------------------------------------------");
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

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Welcome to the Smart Warehouse Stock Tracking platform! " +
                    "\nEnter 1 to query stock " +
                    "\nEnter 2 to subscribe to real-time stock updates ");
            String choice = scanner.nextLine();

            if ("1".equals(choice)) {
                System.out.println("Product name:");
                String productName = scanner.nextLine();
                client.queryStock(productName);
            } else if ("2".equals(choice)) {
                System.out.println("Product name:");
                String productName = scanner.nextLine();
                client.subscribeStockUpdates(productName);
            } else {
                break;
            }
        }
        client.shutdown();
    }
}
