package ru.concurrency.patterns.semaphore;

import java.util.concurrent.Semaphore;

public class Worker implements Runnable {

    private final String name;
    private final Semaphore semaphore;

    public Worker(String name, Semaphore semaphore) {
        this.name = name;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            System.out.println(name + " waiting for permission...");
            semaphore.acquire();
            System.out.println(name + " working...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        semaphore.release();
        System.out.println(name + " releases the resource");
    }
}
