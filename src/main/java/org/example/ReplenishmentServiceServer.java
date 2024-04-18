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
import java.time.LocalDate;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ReplenishmentServiceServer {

    private Server server;

    public void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new ReplenishmentServiceImpl())
                .build()
                .start();
        System.out.println("Server started, listening on " + port);

        // Register server to Consul
        registerToConsul();

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

    public void blockUntilShutdown() throws InterruptedException {
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
                public String calculateEstimatedArrival() {
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
    private void registerToConsul() {
        System.out.println("Registering server to Consul...");

        // Load Consul configuration from consul.properties file
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/replenishment-service.properties")) {
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

    public static void main(String[] args) throws IOException, InterruptedException {
        final ReplenishmentServiceServer server = new ReplenishmentServiceServer();
        server.start();
        server.blockUntilShutdown();
    }
}
