package ru.concurrency.patterns.exchanger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;

public class Writer implements Runnable {

    private Exchanger<Queue<String>> writerExchanger;

    public Writer(Exchanger<Queue<String>> writerExchanger) {
        this.writerExchanger = writerExchanger;
    }

    @Override
    public void run() {
        Queue<String> writerBuffer = new ConcurrentLinkedQueue<>();
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
