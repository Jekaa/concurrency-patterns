package ru.concurrency.patterns.reactor;

import java.nio.channels.SelectionKey;

public interface ChannelHandler {
    void handle(AbstractNioChannel channel, Object obj, SelectionKey key);
}
