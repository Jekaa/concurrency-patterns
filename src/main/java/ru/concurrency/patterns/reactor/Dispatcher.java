package ru.concurrency.patterns.reactor;

import java.nio.channels.SelectionKey;

public interface Dispatcher {
    void onChannelReadEvent(AbstractNioChannel channel, Object obj, SelectionKey key);
    void stop() throws InterruptedException;
}
