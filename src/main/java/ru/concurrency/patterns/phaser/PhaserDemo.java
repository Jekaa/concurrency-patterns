package ru.concurrency.patterns.phaser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class PhaserDemo {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Phaser phaser = new Phaser(1);
        System.out.println("Phase before execution: " + phaser.getPhase());
        executorService.submit(new Worker("thread-0", phaser));
        executorService.submit(new Worker("thread-1", phaser));
        executorService.submit(new Worker("thread-2", phaser));
        executorService.submit(new Worker("thread-3", phaser));
        phaser.arriveAndAwaitAdvance();
        System.out.println("Phase after execution: " + phaser.getPhase());
        executorService.shutdown();
    }
}
