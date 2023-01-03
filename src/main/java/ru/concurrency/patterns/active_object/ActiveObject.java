package ru.concurrency.patterns.active_object;

import java.util.concurrent.PriorityBlockingQueue;

class ActiveObject {

    private final PriorityBlockingQueue<Task> dispatchQueue = new PriorityBlockingQueue<>();

    public ActiveObject() {
        // A priority scheduler
        new Thread(() -> {
            while (true) {
                try {
                    Task task = dispatchQueue.take();
                    System.out.println("Executing task " + task.name);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public void doTask(String name, int priority) {
        dispatchQueue.put(new Task(name, priority));
    }
}
