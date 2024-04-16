package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ReplenishmentServiceClient {
    private final ManagedChannel channel;
    private final ReplenishmentServiceGrpc.ReplenishmentServiceStub asyncStub;

    public ReplenishmentServiceClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        asyncStub = ReplenishmentServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void sendReplenishmentRequests() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<ReplenishmentRequest> requestObserver = asyncStub.manageReplenishment(new StreamObserver<ReplenishmentStatus>() {
            @Override
            public void onNext(ReplenishmentStatus status) {
                System.out.println("\nReplenishment status response: \nProduct ID: " + status.getProductId() +
                        "\nProduct Name: " + status.getProductName() +
                        "\nRequired Quantity: " + status.getRequiredQuantity() +
                        "\nEstimated Arrival Time: " + status.getEstimatedArrival());
                System.out.println("-------------------------------------------------------");
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending data");
                finishLatch.countDown();
            }
        });

        try {
            String addOrder;
            do{
                System.out.println("Welcome to the Smart Warehouse Replenishment platform!" +
                        "\nPlease enter the product information you want to replenish");
                System.out.println("Product ID:");
                int productId = Integer.parseInt(scanner.nextLine());
                System.out.print("Product Name: ");
                String productName = scanner.nextLine();
                System.out.print("Required Quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine());

                ReplenishmentRequest request = ReplenishmentRequest.newBuilder()
                        .setProductId(productId)
                        .setProductName(productName)
                        .setRequiredQuantity(quantity)
                        .build();
                requestObserver.onNext(request);
                TimeUnit.SECONDS.sleep(1);
                System.out.print("Do you want to continue ordering? (y/n): ");
                addOrder = scanner.nextLine();
            }while (addOrder.toLowerCase().equals("y"));

        } catch (RuntimeException e) {
            // Cancel RPC
            requestObserver.onError(e);
            throw e;
        }
        // Mark the end of requests
        requestObserver.onCompleted();

        // Wait until the server has sent back all responses
        finishLatch.await(1, TimeUnit.MINUTES);
    }

    public static void main(String[] args) throws InterruptedException {
        ReplenishmentServiceClient client = new ReplenishmentServiceClient("localhost", 50051);
        try {
            client.sendReplenishmentRequests();
        } finally {
            client.shutdown();
        }
    }
}
