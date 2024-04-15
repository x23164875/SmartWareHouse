package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

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
        CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<ReplenishmentRequest> requestObserver = asyncStub.manageReplenishment(new StreamObserver<ReplenishmentStatus>() {
            @Override
            public void onNext(ReplenishmentStatus status) {
                System.out.println("Replenishment status received: Product ID: " + status.getProductId() +
                        ", Product Name: " + status.getProductName() +
                        ", Required Quantity: " + status.getRequiredQuantity() +
                        ", ETA: " + status.getEstimatedArrival());
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
            // Simulate sending multiple requests
            for (int i = 0; i < 5; i++) {
                ReplenishmentRequest request = ReplenishmentRequest.newBuilder()
                        .setProductId(i)
                        .setProductName("printer" + i)
                        .setRequiredQuantity(5)
                        .build();
                requestObserver.onNext(request);
            }
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
