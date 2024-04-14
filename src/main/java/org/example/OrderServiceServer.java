package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OrderServiceServer {

    private Server server;

    public void start(int port) throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new OrderServiceImpl())
                .build()
                .start();
        System.out.println("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server");
            try {
                this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
        @Override
        public StreamObserver<Order> streamOrders(StreamObserver<OrderConfirmation> responseObserver) {
            return new StreamObserver<Order>() {
                @Override
                public void onNext(Order order) {
                    System.out.println("Received order from client: " + order.getOrderId());
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    // Send confirmation back to client
                    OrderConfirmation confirmation = OrderConfirmation.newBuilder()
                            .setMessage("All orders processed successfully.")
                            .build();
                    responseObserver.onNext(confirmation);
                    responseObserver.onCompleted();
                }
            };
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final OrderServiceServer server = new OrderServiceServer();
        server.start(8080);
        server.blockUntilShutdown();
    }
}
