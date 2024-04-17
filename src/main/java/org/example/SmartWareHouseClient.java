package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SmartWareHouseClient {
    private final ManagedChannel stockChannel;
    private final ManagedChannel orderChannel;
    private final ManagedChannel replenishChannel;
    private final StockTrackingServiceGrpc.StockTrackingServiceBlockingStub blockingStub;
    private final StockTrackingServiceGrpc.StockTrackingServiceStub asyncStub;
    private final OrderServiceGrpc.OrderServiceStub orderStub;
    private final ReplenishmentServiceGrpc.ReplenishmentServiceStub replenishStub;
    private int orderId = 1;

    public SmartWareHouseClient(String host, int stockPort, int orderPort, int replenishPort) {
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

    // Add shutdown method for all channels
    public void shutdown() throws InterruptedException {
        stockChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        orderChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        replenishChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void start() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Choose service:" +
                    "\n1. Stock Tracking" +
                    "\n2. Subscribe Stock Updates" +
                    "\n3. Order Service" +
                    "\n4. Replenishment Service" +
                    "\n5. Exit");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    queryStock(scanner);
                    break;
                case "2":
                    subscribeStockUpdates(scanner);
                    break;
                case "3":
                    streamOrders(scanner);
                    break;
                case "4":
                    sendReplenishmentRequests(scanner);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid option, please choose again.");
                    break;
            }
        }
    }

    // simple RPC: Check the inventory status of a specific item
    public void queryStock(Scanner scanner) {
        System.out.println("Enter product name:");
        String productName = scanner.nextLine();
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
    public void subscribeStockUpdates(Scanner scanner) {
        System.out.println("Enter product name:");
        String productName = scanner.nextLine();
        StockQueryRequest request = StockQueryRequest.newBuilder()
                .setProductName(productName)
                .build();

        CountDownLatch latch = new CountDownLatch(1);
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
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed.");
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

    // client-side streaming RPC: Client sends a series of orders
    public void streamOrders(Scanner scanner) {
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

        StreamObserver<Order> requestObserver = orderStub.streamOrders(responseObserver);
        try {
            String addMore;
            do {
                System.out.println("Welcome to the smart warehouse ordering platform! " +
                        "\nPlease enter the product information you want to order");
                System.out.println("Product ID:");
                int productId = Integer.parseInt(scanner.nextLine());
                System.out.println("Product name:");
                String productName = scanner.nextLine();
                System.out.println("Product quantity:");
                int quantity = Integer.parseInt(scanner.nextLine());
                System.out.println("Customer name:");
                String customerName = scanner.nextLine();

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

    // bidirectional streaming RPC: Replenishment requests and status updates interact in real time
    public void sendReplenishmentRequests(Scanner scanner) throws InterruptedException {
        CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<ReplenishmentRequest> requestObserver = replenishStub.manageReplenishment(new StreamObserver<ReplenishmentStatus>() {
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
        SmartWareHouseClient client = new SmartWareHouseClient("localhost", 9090, 8080, 50051);
        try {
            client.start();
        } finally {
            client.shutdown();
        }
    }
}
