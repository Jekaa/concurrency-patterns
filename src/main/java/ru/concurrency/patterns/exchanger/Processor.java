package ru.concurrency.patterns.exchanger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;

public class Processor implements Runnable {

    private final Exchanger<Queue<String>> readerExchanger;
    private final Exchanger<Queue<String>> writerExchanger;
    private Queue<String> processorBuffer;
    private Queue<String> writerBuffer;

    public Processor(Exchanger<Queue<String>> readerExchanger, Exchanger<Queue<String>> writerExchanger) {
        this.readerExchanger = readerExchanger;
        this.writerExchanger = writerExchanger;
        this.processorBuffer = new ConcurrentLinkedQueue<>();
        this.writerBuffer = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        try {
            processorBuffer = readerExchanger.exchange(processorBuffer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            var data = processorBuffer.poll();
            writerBuffer.add(data);
            System.out.println(Thread.currentThread().getName() + " processed: " + data);
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
    }
}
