package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class OrderServiceClient {

    private final ManagedChannel channel;
    private final OrderServiceGrpc.OrderServiceStub asyncStub;

    public OrderServiceClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        asyncStub = OrderServiceGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(12, TimeUnit.SECONDS);
    }

    public void streamOrders() {
        StreamObserver<OrderConfirmation> responseObserver = new StreamObserver<OrderConfirmation>() {
            @Override
            public void onNext(OrderConfirmation confirmation) {
                System.out.println("Server response: " + confirmation.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream is completed.");
            }
        };

        StreamObserver<Order> requestObserver = asyncStub.streamOrders(responseObserver);
        try {
            // Send multiple orders
            requestObserver.onNext(Order.newBuilder().setOrderId(1).setProductId(1).setProductName("Printer").setProductQuantity(7).setCustomerName("Anda Brown").setTotalPrice(350).setOrderStatus("pending").build());
            requestObserver.onNext(Order.newBuilder().setOrderId(2).setProductId(1).setProductName("Printer").setProductQuantity(5).setCustomerName("Bob Smith").setTotalPrice(250).setOrderStatus("pending").build());
            // Mark the end of requests
            requestObserver.onCompleted();

            // Sleep for a bit to ensure the server responds.
            Thread.sleep(1000);
        } catch (RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        OrderServiceClient client = new OrderServiceClient("localhost", 8080);
        try {
            client.streamOrders();
        } finally {
            client.shutdown();
        }
    }
}
