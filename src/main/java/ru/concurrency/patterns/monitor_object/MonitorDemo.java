package ru.concurrency.patterns.monitor_object;

public class MonitorDemo {

    public static void main(String[] args) {
        MonitorQueue monitorQueue = new MonitorQueue();
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; ++i) {
                try {
                    monitorQueue.append(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 10; i < 20; ++i) {
                try {
                    monitorQueue.append(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread3 = new Thread(() -> {
            for (int i = 0; i < 20; ++i) {
                try {
                    System.out.println("Poll monitor queue: " + monitorQueue.poll());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread3.start();
        thread1.start();
        thread2.start();
    }
}
