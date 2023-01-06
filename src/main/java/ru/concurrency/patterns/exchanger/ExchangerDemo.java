package ru.concurrency.patterns.exchanger;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;

import static java.util.concurrent.CompletableFuture.runAsync;

public class ExchangerDemo {

    private static final int BUFFER_SIZE = 64;

    public static void main(String[] args) {

        Exchanger<Queue<String>> readerExchanger = new Exchanger<>();
        Exchanger<Queue<String>> writerExchanger = new Exchanger<>();

        Runnable reader = () -> {
            Queue<String> readerBuffer = new ConcurrentLinkedQueue<>();
            while (true) {
                var uuid = UUID.randomUUID().toString();
                readerBuffer.add(uuid);
                System.out.println("Generated: " + uuid);
                if (readerBuffer.size() >= BUFFER_SIZE) {
                    try {
                        readerBuffer = readerExchanger.exchange(readerBuffer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Runnable processor = () -> {
            Queue<String> processorBuffer = new ConcurrentLinkedQueue<>();
            Queue<String> writerBuffer = new ConcurrentLinkedQueue<>();
            try {
                processorBuffer = readerExchanger.exchange(processorBuffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                var data = processorBuffer.poll();
                writerBuffer.add(data);
                System.out.println("Processed: " + data);
                if (processorBuffer.isEmpty()) {
                    try {
                        processorBuffer = readerExchanger.exchange(processorBuffer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        writerBuffer = writerExchanger.exchange(writerBuffer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Runnable writer = () -> {
            Queue<String> writerBuffer = new ConcurrentLinkedQueue<>();
            try {
                writerBuffer = writerExchanger.exchange(writerBuffer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                System.out.println("Write: " + writerBuffer.poll());
                if (writerBuffer.isEmpty()) {
                    try {
                        writerBuffer = writerExchanger.exchange(writerBuffer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        CompletableFuture.allOf(
                runAsync(reader),
                runAsync(processor),
                runAsync(writer)).join();
    }
}
