package ru.concurrency.patterns.double_checked_locking;

public class DoubleCheckedLockingDemo {

    public static void main(String[] args) {
        final Singleton[] singletons = new Singleton[40];
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < singletons.length/2; i++) {
                singletons[i] = Singleton.getInstance();
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = singletons.length/2; i < singletons.length; i++) {
                singletons[i] = Singleton.getInstance();
            }
        });
        thread1.start();
        thread2.start();
        for (int i = 0; i < singletons.length; i++) {
            System.out.println("Singleton[" + i + "] = " + singletons[i]);
        }
    }
}
