package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class StockTrackingServiceServer extends StockTrackingServiceGrpc.StockTrackingServiceImplBase {


    public void queryStock(StockQueryRequest request, StreamObserver<StockStatus> responseObserver) {
        // simple RPC: Check the inventory status of a specific item
        StockStatus status = StockStatus.newBuilder()
                .setProductId(1)
                .setProductName(request.getProductName())
                .setCurrentQuantity(99)
                .setStatus("stock available")
                .build();
        responseObserver.onNext(status);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribeStockUpdates(StockQueryRequest request, StreamObserver<StockStatus> responseObserver) {
        // server-side streaming RPC: Send inventory status regularly
        for (int i = 0; i < 10; i++) {
            StockStatus status = StockStatus.newBuilder()
                    .setProductId(1)
                    .setProductName(request.getProductName())
                    .setCurrentQuantity(99 - i * 10)
                    .setStatus("stock available")
                    .build();
            responseObserver.onNext(status);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
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
