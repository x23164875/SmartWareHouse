package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReplenishmentServiceServer {

    private Server server;

    private void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new ReplenishmentServiceImpl())
                .build()
                .start();
        System.out.println("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                ReplenishmentServiceServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.out.println("*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class ReplenishmentServiceImpl extends ReplenishmentServiceGrpc.ReplenishmentServiceImplBase {

        @Override
        public StreamObserver<ReplenishmentRequest> manageReplenishment(
                StreamObserver<ReplenishmentStatus> responseObserver) {
            return new StreamObserver<ReplenishmentRequest>() {
                @Override
                public void onNext(ReplenishmentRequest req) {
                    System.out.println("Received replenishment request for product ID: " + req.getProductId() + ", Quantity: " + req.getRequiredQuantity());

                    // Simulate some processing and send back a status
                    ReplenishmentStatus status = ReplenishmentStatus.newBuilder()
                            .setProductId(req.getProductId())
                            .setProductName(req.getProductName())
                            .setRequiredQuantity(req.getRequiredQuantity())
                            .setEstimatedArrival("2024-04-25")
                            .build();
                    responseObserver.onNext(status);
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final ReplenishmentServiceServer server = new ReplenishmentServiceServer();
        server.start();
        server.blockUntilShutdown();
    }
}
