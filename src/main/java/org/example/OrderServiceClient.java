package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class OrderServiceClient {

    private final ManagedChannel channel;
    private final OrderServiceGrpc.OrderServiceStub asyncStub;
    private int orderId = 1;

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
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        try {
            String addMore = "y";
            do {
                System.out.println("Welcome to the smart warehouse ordering platform, " +
                        "please enter the product information you want to order");
                System.out.println("Product ID:");
                int productId = Integer.parseInt(scanner.nextLine());
                System.out.println("Product name:");
                String productName = scanner.nextLine();
                System.out.println("Product quantity:");
                int quantity = Integer.parseInt(scanner.nextLine());
                System.out.println("Customer name:");
                String customerName = scanner.nextLine();

                int totalPrice = 200 + random.nextInt(500);
                String orderStatus = "pending";

                Order order = Order.newBuilder()
                        .setOrderId(orderId++)
                        .setProductId(productId)
                        .setProductName(productName)
                        .setProductQuantity(quantity)
                        .setCustomerName(customerName)
                        .setTotalPrice(totalPrice)
                        .setOrderStatus(orderStatus)
                        .build();
                requestObserver.onNext(order);

                System.out.println("Continue ordering? (y/n)");
                addMore = scanner.nextLine();
            }while (addMore.equalsIgnoreCase("y"));
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
