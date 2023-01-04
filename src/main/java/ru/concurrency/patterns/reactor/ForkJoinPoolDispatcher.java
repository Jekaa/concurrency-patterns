package ru.concurrency.patterns.reactor;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForkJoinPoolDispatcher implements Dispatcher {

    private final ExecutorService executorService;

    public ForkJoinPoolDispatcher(int parallelFactor) {
        this.executorService = Executors.newWorkStealingPool(parallelFactor);
    }

    @Override
    public void onChannelReadEvent(AbstractNioChannel channel, Object obj, SelectionKey key) {
        executorService.execute(() -> channel.getHandler().handle(channel, obj, key));
    }

    @Override
    public void stop() throws InterruptedException {
        executorService.shutdown();
    }
}
