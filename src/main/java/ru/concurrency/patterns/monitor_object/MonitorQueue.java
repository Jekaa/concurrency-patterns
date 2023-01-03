package ru.concurrency.patterns.monitor_object;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorQueue {
    private LinkedList<Integer> queue = new LinkedList<>();
    final Lock lock = new ReentrantLock();
    final Condition notEmpty = lock.newCondition();

    public void append(int num) throws InterruptedException {
        lock.lock();
        try {
            queue.addLast(num);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public int poll() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();
            }
        } finally {
            lock.unlock();
        }
        return queue.removeFirst();
    }
}
