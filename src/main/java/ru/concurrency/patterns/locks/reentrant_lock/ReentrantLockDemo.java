package ru.concurrency.patterns.locks.reentrant_lock;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Worker worker = new Worker(lock);
        new Thread(worker, "thread-0").start();
        new Thread(worker, "thread-1").start();
    }
}
