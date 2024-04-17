package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.swing.*;
import java.util.Random;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SmartWarehouseGUI {
    private final ManagedChannel stockChannel;
    private final ManagedChannel orderChannel;
    private final ManagedChannel replenishChannel;
    private final StockTrackingServiceGrpc.StockTrackingServiceBlockingStub blockingStub;
    private final StockTrackingServiceGrpc.StockTrackingServiceStub asyncStub;
    private final OrderServiceGrpc.OrderServiceStub orderStub;
    private final ReplenishmentServiceGrpc.ReplenishmentServiceStub replenishStub;
    private int orderId = 1;

    public SmartWarehouseGUI(String host, int stockPort, int orderPort, int replenishPort) {
        this.stockChannel = ManagedChannelBuilder.forAddress(host, stockPort)
                .usePlaintext()
                .build();
        this.orderChannel = ManagedChannelBuilder.forAddress(host, orderPort)
                .usePlaintext()
                .build();
        this.replenishChannel = ManagedChannelBuilder.forAddress(host, replenishPort)
                .usePlaintext()
                .build();

        this.blockingStub = StockTrackingServiceGrpc.newBlockingStub(stockChannel);
        this.asyncStub = StockTrackingServiceGrpc.newStub(stockChannel);
        this.orderStub = OrderServiceGrpc.newStub(orderChannel);
        this.replenishStub = ReplenishmentServiceGrpc.newStub(replenishChannel);
    }

    public void start() throws InterruptedException {
        String[] options = {"Stock Tracking", "Subscribe Stock Updates", "Order Service", "Replenishment Service", "Exit"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(null, "Welcome to Smart Warehouse,choose service:", "Smart Warehouse Services",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0: // Stock Tracking
                    queryStock();
                    break;
                case 1: // Subscribe Stock Updates
                    subscribeStockUpdates();
                    break;
                case 2: // Order Service
                    streamOrders();
                    break;
                case 3: // Replenishment Service
                    sendReplenishmentRequests();
                    break;
                case 4: // Exit
                    return;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option, please choose again.", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
    }
    // Add shutdown method for all channels
    public void shutdown() throws InterruptedException {
        stockChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        orderChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        replenishChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    // simple RPC: Check the inventory status of a specific item
    public void queryStock() {
        String productName = JOptionPane.showInputDialog("Enter product name:");
        if (productName != null && !productName.isEmpty()) {
            StockQueryRequest request = StockQueryRequest.newBuilder()
                    .setProductName(productName)
                    .build();
            StockStatus response = blockingStub.queryStock(request);
            JOptionPane.showMessageDialog(null, "Received response: \nProduct ID: " + response.getProductId()
                    + "\nProduct Name: " + response.getProductName()
                    + "\nQuantity: " + response.getCurrentQuantity()
                    + "\nStatus: " + response.getStatus());
        }
    }

    // server-side streaming RPC: Send inventory status regularly
    public void subscribeStockUpdates() {
        String productName = JOptionPane.showInputDialog("Enter product name for updates:");
        if (productName != null && !productName.isEmpty()) {
            StockQueryRequest request = StockQueryRequest.newBuilder()
                    .setProductName(productName)
                    .build();

            CountDownLatch latch = new CountDownLatch(1);
            StreamObserver<StockStatus> responseObserver = new StreamObserver<StockStatus>() {
                @Override
                public void onNext(StockStatus value) {
                    JOptionPane.showMessageDialog(null, "Response: \nProduct ID: " + value.getProductId()
                            + "\nProduct Name: " + value.getProductName()
                            + "\nQuantity: " + value.getCurrentQuantity()
                            + "\nStatus: " + value.getStatus());
                }

                @Override
                public void onError(Throwable t) {
                    JOptionPane.showMessageDialog(null, "Error during SubscribeStockUpdates: " + t.getMessage());
                    latch.countDown();
                }

                @Override
                public void onCompleted() {
                    JOptionPane.showMessageDialog(null, "Stream completed.");
                    latch.countDown();
                }
            };
            asyncStub.subscribeStockUpdates(request, responseObserver);
            try {
                latch.await(); // Wait for the count to reach zero, indicating that all messages have been processed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // client-side streaming RPC: Client sends a series of orders
    public void streamOrders() {
        StreamObserver<OrderConfirmation> responseObserver = new StreamObserver<OrderConfirmation>() {
            @Override
            public void onNext(OrderConfirmation confirmation) {
                JOptionPane.showMessageDialog(null, "Server response: " + confirmation.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                JOptionPane.showMessageDialog(null, "Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                JOptionPane.showMessageDialog(null, "Stream is completed.");
            }
        };

        StreamObserver<Order> requestObserver = orderStub.streamOrders(responseObserver);
        try {
            String addMore;
            do {
                int productId = Integer.parseInt(JOptionPane.showInputDialog("Product ID:"));
                String productName = JOptionPane.showInputDialog("Product name:");
                int quantity = Integer.parseInt(JOptionPane.showInputDialog("Product quantity:"));
                String customerName = JOptionPane.showInputDialog("Customer name:");
                int totalPrice = 200 + new Random().nextInt(500);
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

                addMore = JOptionPane.showInputDialog("Continue ordering? (y/n)");
            } while ("y".equalsIgnoreCase(addMore));
            requestObserver.onCompleted();
            Thread.sleep(1000);
        } catch (RuntimeException | InterruptedException e) {
            requestObserver.onError(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
    public void sendReplenishmentRequests() throws InterruptedException {
        CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<ReplenishmentRequest> requestObserver = replenishStub.manageReplenishment(new StreamObserver<ReplenishmentStatus>() {
            @Override
            public void onNext(ReplenishmentStatus status) {
                JOptionPane.showMessageDialog(null, "Replenishment status response: \nProduct ID: " + status.getProductId() +
                        "\nProduct Name: " + status.getProductName() +
                        "\nRequired Quantity: " + status.getRequiredQuantity() +
                        "\nEstimated Arrival Time: " + status.getEstimatedArrival());
            }

            @Override
            public void onError(Throwable t) {
                JOptionPane.showMessageDialog(null, "Error: " + t.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                JOptionPane.showMessageDialog(null, "Server has completed sending data.");
                finishLatch.countDown();
            }
        });

        try {
            String addOrder;
            do {
                int productId = Integer.parseInt(JOptionPane.showInputDialog("Product ID:"));
                String productName = JOptionPane.showInputDialog("Product Name:");
                int quantity = Integer.parseInt(JOptionPane.showInputDialog("Required Quantity:"));

                ReplenishmentRequest request = ReplenishmentRequest.newBuilder()
                        .setProductId(productId)
                        .setProductName(productName)
                        .setRequiredQuantity(quantity)
                        .build();
                requestObserver.onNext(request);

                addOrder = JOptionPane.showInputDialog("Do you want to continue ordering? (y/n):");
            } while ("y".equalsIgnoreCase(addOrder));

        } catch (RuntimeException e) {
            requestObserver.onError(e);
            throw e;
        }
        // Mark the end of requests
        requestObserver.onCompleted();

        // Wait until the server has sent back all responses
        finishLatch.await(1, TimeUnit.MINUTES);
    }
    public static void main(String[] args) throws InterruptedException {
        SmartWarehouseGUI client = new SmartWarehouseGUI("localhost", 9090, 8080, 50051);
        try {
            client.start();
        } finally {
            client.shutdown();
        }
    }
}