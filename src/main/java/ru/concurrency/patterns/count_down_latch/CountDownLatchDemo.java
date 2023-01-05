package ru.concurrency.patterns.count_down_latch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class CountDownLatchDemo {

    private static final int NUM_WORKERS = 5;

    public static void main(String[] args) throws InterruptedException {
        List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch countDownLatch = new CountDownLatch(NUM_WORKERS);
        List<Thread> workers = Stream
                .generate(() -> new Thread(new Worker(outputScraper, countDownLatch)))
                .limit(NUM_WORKERS)
                .toList();

        workers.forEach(Thread::start);
        countDownLatch.await();
        outputScraper.add("Latch released");
        outputScraper.forEach(System.out::println);
    }
}
