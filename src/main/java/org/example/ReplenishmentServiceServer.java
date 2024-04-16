package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;
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
                    System.out.println("Received replenishment request for product ID: " +
                            req.getProductId() + ", ProductName: " + req.getProductName() +
                                    ", Quantity: " + req.getRequiredQuantity());

                    // Calculate estimated arrival time
                    String estimatedArrival = calculateEstimatedArrival();

                    ReplenishmentStatus status = ReplenishmentStatus.newBuilder()
                            .setProductId(req.getProductId())
                            .setProductName(req.getProductName())
                            .setRequiredQuantity(req.getRequiredQuantity())
                            .setEstimatedArrival(estimatedArrival)
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
                private String calculateEstimatedArrival() {
                    // Calculate estimated arrival time
                    Random random = new Random();
                    int minDay = (int) LocalDate.of(2024, 4, 21).toEpochDay();
                    int maxDay = (int) LocalDate.of(2024, 5, 31).toEpochDay();
                    long randomDay = minDay + random.nextInt(maxDay - minDay);
                    return LocalDate.ofEpochDay(randomDay).toString();
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
