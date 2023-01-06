package ru.concurrency.patterns.exchanger;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;

public class Reader implements Runnable {

    private final Exchanger<Queue<String>> readerExchanger;
    private final int bufferSize;

    public Reader(Exchanger<Queue<String>> readerExchanger, int bufferSize) {
        this.readerExchanger = readerExchanger;
        this.bufferSize = bufferSize;
    }

    @Override
    public void run() {
        Queue<String> readerBuffer = new ConcurrentLinkedQueue<>();
        while (true) {
            var uuid = UUID.randomUUID().toString();
            readerBuffer.add(uuid);
            System.out.println(Thread.currentThread().getName() + " generated: " + uuid);
            if (readerBuffer.size() >= bufferSize) {
                try {
                    readerBuffer = readerExchanger.exchange(readerBuffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
