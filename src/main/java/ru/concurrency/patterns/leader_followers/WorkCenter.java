package ru.concurrency.patterns.leader_followers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorkCenter {

    private Worker leaderWorker;
    private final List<Worker> workers = new CopyOnWriteArrayList<>();

    public void createWorkers(int numberOfWorkers, TaskSet taskSet, TaskHandler taskHandler) {
        for (var id = 1; id <= numberOfWorkers; id++) {
            var worker = new Worker(id, this, taskSet, taskHandler);
            workers.add(worker);
        }
        promoteLeader();
    }

    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    public void removeWorker(Worker worker) {
        workers.remove(worker);
    }

    public Worker getLeader() {
        return leaderWorker;
    }

    public void promoteLeader() {
        Worker leader = null;
        if (workers.size() > 0) {
            leader = workers.get(0);
        }
        this.leaderWorker = leader;
    }

    public List<Worker> getWorkers() {
        return workers;
    }
}
