package ru.concurrency.patterns.reactor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientDemo {

    private final ExecutorService service = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws IOException {
        var client = new ClientDemo();
        client.start();
    }

    public void start() throws IOException {
        System.out.println("Starting logging clients");
        service.execute(new TcpLoggingClient("Client 1", 16666));
        service.execute(new TcpLoggingClient("Client 2", 16667));
        service.execute(new UdpLoggingClient("Client 3", 16668));
        service.execute(new UdpLoggingClient("Client 4", 16669));
    }

    public void stop() {
        service.shutdown();
        if (!service.isTerminated()) {
            service.shutdownNow();
            try {
                service.awaitTermination(1000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("exception awaiting termination" + e.getMessage());
            }
        }
        System.out.println("Logging clients stopped");
    }

    private static void artificialDelayOf(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("sleep interrupted" + e.getMessage());
        }
    }

    record TcpLoggingClient(String clientName, int serverPort) implements Runnable {

        @Override
        public void run() {
            try (var socket = new Socket(InetAddress.getLocalHost(), serverPort)) {
                var outputStream = socket.getOutputStream();
                var writer = new PrintWriter(outputStream);
                sendLogRequests(writer, socket.getInputStream());
            } catch (IOException e) {
                System.out.println("error sending requests" + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        private void sendLogRequests(PrintWriter writer, InputStream inputStream) throws IOException {
            for (var i = 0; i < 4; i++) {
                writer.println(clientName + " - Log request: " + i);
                writer.flush();

                var data = new byte[1024];
                var read = inputStream.read(data, 0, data.length);
                if (read == 0) {
                    System.out.println("Read zero bytes");
                } else {
                    System.out.println(new String(data, 0, read));
                }

                artificialDelayOf(100);
            }
        }

    }

    static class UdpLoggingClient implements Runnable {
        private final String clientName;
        private final InetSocketAddress remoteAddress;

        public UdpLoggingClient(String clientName, int port) throws UnknownHostException {
            this.clientName = clientName;
            this.remoteAddress = new InetSocketAddress(InetAddress.getLocalHost(), port);
        }

        @Override
        public void run() {
            try (var socket = new DatagramSocket()) {
                for (var i = 0; i < 4; i++) {

                    var message = clientName + " - Log request: " + i;
                    var bytes = message.getBytes();
                    var request = new DatagramPacket(bytes, bytes.length, remoteAddress);

                    socket.send(request);

                    var data = new byte[1024];
                    var reply = new DatagramPacket(data, data.length);
                    socket.receive(reply);
                    if (reply.getLength() == 0) {
                        System.out.println("Read zero bytes");
                    } else {
                        System.out.println(new String(reply.getData(), 0, reply.getLength()));
                    }

                    artificialDelayOf(100);
                }
            } catch (IOException e1) {
                System.out.println("error sending packets" + e1.getMessage());
            }
        }
    }
}
