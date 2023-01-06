package ru.concurrency.patterns.phaser;

import java.util.concurrent.Phaser;

public class Worker implements Runnable {

    private final String name;
    private final Phaser phaser;

    Worker(String name, Phaser ph) {
        this.name = name;
        this.phaser = ph;
        ph.register();
    }

    @Override
    public void run() {
        phaser.arriveAndAwaitAdvance();
        try {
            System.out.println("Worker " + name + " start...");
            Thread.sleep(100);
            System.out.println("Worker " + name + " finish!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        phaser.arriveAndDeregister();
    }
}
