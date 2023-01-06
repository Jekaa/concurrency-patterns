package ru.concurrency.patterns.semaphore;

import java.util.concurrent.Semaphore;

public class SemaphoreDemo {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(1);
        new Thread(new Worker("thread-0", semaphore)).start();
        new Thread(new Worker("thread-1", semaphore)).start();
        new Thread(new Worker("thread-2", semaphore)).start();
        new Thread(new Worker("thread-3", semaphore)).start();
    }
}
