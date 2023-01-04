package ru.concurrency.patterns.leader_followers;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

public class LeaderFollowersDemo {

    public static void main(String[] args) throws InterruptedException {
        var taskSet = new TaskSet();
        var taskHandler = new TaskHandler();
        var workCenter = new WorkCenter();
        workCenter.createWorkers(4, taskSet, taskHandler);
        execute(workCenter, taskSet);
    }

    private static void execute(WorkCenter workCenter, TaskSet taskSet) throws InterruptedException {
        var workers = workCenter.getWorkers();
        var exec = Executors.newFixedThreadPool(workers.size());
        workers.forEach(exec::submit);
        Thread.sleep(1000);
        addTasks(taskSet);
        exec.awaitTermination(2, TimeUnit.SECONDS);
        exec.shutdownNow();
    }

    private static void addTasks(TaskSet taskSet) throws InterruptedException {
        var rand = RandomGenerator.getDefault();
        for (var i = 0; i < 5; i++) {
            var time = Math.abs(rand.nextInt(1000));
            taskSet.addTask(new Task(time));
        }
    }
}
