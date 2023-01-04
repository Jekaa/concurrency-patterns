package ru.concurrency.patterns.leader_followers;

public class TaskHandler {

    public void handleTask(Task task) throws InterruptedException {
        var time = task.getTime();
        Thread.sleep(time);
        System.out.println("It takes " + time + " milliseconds to finish the task");
        task.setFinished();
    }
}
