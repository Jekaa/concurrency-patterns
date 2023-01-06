package ru.concurrency.patterns.exchanger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;

public class Writer implements Runnable {

    private final Exchanger<Queue<String>> writerExchanger;
    private Queue<String> writerBuffer;

    public Writer(Exchanger<Queue<String>> writerExchanger) {
        this.writerExchanger = writerExchanger;
        this.writerBuffer = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        try {
            writerBuffer = writerExchanger.exchange(writerBuffer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            System.out.println(Thread.currentThread().getName() + " write: " + writerBuffer.poll());
            if (writerBuffer.isEmpty()) {
                try {
                    writerBuffer = writerExchanger.exchange(writerBuffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
