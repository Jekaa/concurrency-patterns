package ru.concurrency.patterns.active_object;

class Task implements Comparable<Task> {
    // smaller number means higher priority
    int priority;
    String name;

    Task(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority);
    }
}
