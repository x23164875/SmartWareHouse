package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StockTrackingServiceServer extends StockTrackingServiceGrpc.StockTrackingServiceImplBase {
    private Random random = new Random();
    // simple RPC: Check the inventory status of a specific item
    public void queryStock(StockQueryRequest request, StreamObserver<StockStatus> responseObserver) {
        int productId = random.nextInt(100) ;
        int quantity = random.nextInt(100);
        String status = determineStatus(quantity);
        StockStatus currentStatus = StockStatus.newBuilder()
                .setProductId(productId)
                .setProductName(request.getProductName())
                .setCurrentQuantity(quantity)
                .setStatus(status)
                .build();
        responseObserver.onNext(currentStatus);
        responseObserver.onCompleted();
    }

    // server-side streaming RPC: Send inventory status regularly
    @Override
    public void subscribeStockUpdates(StockQueryRequest request, StreamObserver<StockStatus> responseObserver) {
        int productId = random.nextInt(100) ;
        int quantity;
        int num = 100; // Starting quantity

        for (int i = 0; i < 10; i++) {
            num = num - random.nextInt(20) ;
            String status = determineStatus(num);
            quantity = Math.max(num, 0);  // Ensure quantity never goes below zero

            StockStatus statusUpdate = StockStatus.newBuilder()
                    .setProductId(productId)
                    .setProductName(request.getProductName())
                    .setCurrentQuantity(quantity)
                    .setStatus(status)
                    .build();
            responseObserver.onNext(statusUpdate);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

    public String determineStatus(int num) {
        if (num <= 0) {
            return "out of Stock";
        } else if (num <= 9) {
            return "low stock";
        } else {
            return "stock available";
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090)
                .addService(new StockTrackingServiceServer())
                .build();

        server.start();
        System.out.println("Server started, listening on " + 9090);

        server.awaitTermination();
    }
}
