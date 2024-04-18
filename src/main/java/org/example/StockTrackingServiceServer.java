package org.example;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StockTrackingServiceServer {

    private Server server;

    public void start(int port) throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new StockTrackingServiceImpl())
                .build()
                .start();
        System.out.println("Server started, listening on " + port);

        // Register server to Consul
        registerToConsul();

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

    private void registerToConsul() {
        System.out.println("Registering server to Consul...");

        // Load Consul configuration from consul.properties file
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/stock-tracking-service.properties")) {
            props.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Extract Consul configuration properties
        String consulHost = props.getProperty("consul.host");
        int consulPort = Integer.parseInt(props.getProperty("consul.port"));
        String serviceName = props.getProperty("consul.service.name");
        int servicePort = Integer.parseInt(props.getProperty("consul.service.port"));
        String healthCheckInterval = props.getProperty("consul.service.healthCheckInterval");

        // Get host address
        String hostAddress;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        // Create a Consul client
        ConsulClient consulClient = new ConsulClient(consulHost, consulPort);

        // Define service details
        NewService newService = new NewService();
        newService.setName(serviceName);
        newService.setPort(servicePort);
        newService.setAddress(hostAddress); // Set host address

        // Register service with Consul
        consulClient.agentServiceRegister(newService);

        // Print registration success message
        System.out.println("Server registered to Consul successfully. Host: " + hostAddress);
    }


    static class StockTrackingServiceImpl extends StockTrackingServiceGrpc.StockTrackingServiceImplBase {
        private Random random = new Random();

        // simple RPC: Check the inventory status of a specific item
        public void queryStock(StockQueryRequest request, StreamObserver<StockStatus> responseObserver) {
            int productId = random.nextInt(100);
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
            int productId = random.nextInt(100);
            int quantity;
            int num = 100; // Starting quantity

            for (int i = 0; i < 10; i++) {
                num = num - random.nextInt(20);
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
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        final StockTrackingServiceServer server = new StockTrackingServiceServer();
        server.start(9090); // Or whatever port you need
        server.blockUntilShutdown();
    }
}