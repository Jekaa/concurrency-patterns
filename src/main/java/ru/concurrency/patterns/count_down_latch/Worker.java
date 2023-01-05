package ru.concurrency.patterns.count_down_latch;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Worker implements Runnable {

    private List<String> outputScraper;
    private CountDownLatch countDownLatch;

    public Worker(List<String> outputScraper, CountDownLatch countDownLatch) {
        this.outputScraper = outputScraper;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println("Worker done...");
        outputScraper.add("Counted down");
        countDownLatch.countDown();
    }
}
