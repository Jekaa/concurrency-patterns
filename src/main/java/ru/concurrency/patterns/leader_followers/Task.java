package ru.concurrency.patterns.leader_followers;

public class Task {

    private final int time;

    private boolean finished;

    public Task(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setFinished() {
        this.finished = true;
    }

    public boolean isFinished() {
        return this.finished;
    }
}
