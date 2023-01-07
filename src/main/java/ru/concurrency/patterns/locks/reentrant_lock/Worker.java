package ru.concurrency.patterns.locks.reentrant_lock;

import java.util.concurrent.locks.ReentrantLock;

class Worker implements Runnable{

    ReentrantLock lock;

    public Worker(ReentrantLock lock) {
        this.lock=lock;
    }

    public void run(){

        System.out.println(Thread.currentThread().getName() + " is waiting to get the lock");
        lock.lock();
        System.out.println(Thread.currentThread().getName() + " has got the  lock.");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " has queued Threads = " + lock.hasQueuedThreads());
        System.out.println(Thread.currentThread().getName() + " has released the lock.");
        lock.unlock();
    }
}
