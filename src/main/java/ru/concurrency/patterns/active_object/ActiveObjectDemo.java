package ru.concurrency.patterns.active_object;

public class ActiveObjectDemo {
    public static void main(String[] args) {

        ActiveObject activeObject = new ActiveObject();

        Thread thread1 = new Thread(() -> activeObject.doTask("1", 2));
        Thread thread2 = new Thread(() -> activeObject.doTask("2", 0));
        Thread thread3 = new Thread(() -> activeObject.doTask("3", 1));

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
