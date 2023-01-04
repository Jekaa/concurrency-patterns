package ru.concurrency.patterns.reactor;

/*
 * This project is licensed under the MIT license. Module model-view-viewmodel is using ZK framework licensed under LGPL (see lgpl-3.0.txt).
 *
 * The MIT License
 * Copyright © 2014-2022 Ilkka Seppälä
 */

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractNioChannel {

    private final SelectableChannel channel;
    private final ChannelHandler handler;
    private final Map<SelectableChannel, Queue<Object>> channelToPendingWrites;
    private Reactor reactor;

    protected AbstractNioChannel(ChannelHandler handler, SelectableChannel channel) {
        this.handler = handler;
        this.channel = channel;
        this.channelToPendingWrites = new ConcurrentHashMap<>();
    }

    void setReactor(Reactor reactor) {
        this.reactor = reactor;
    }

    public SelectableChannel getJavaChannel() {
        return channel;
    }

    public abstract int getInterestedOps();

    public abstract void bind() throws IOException;

    public abstract Object read(SelectionKey key) throws IOException;

    public ChannelHandler getHandler() {
        return handler;
    }

    void flush(SelectionKey key) throws IOException {
        var pendingWrites = channelToPendingWrites.get(key.channel());
        Object pendingWrite;
        while ((pendingWrite = pendingWrites.poll()) != null) {
            doWrite(pendingWrite, key);
        }
        reactor.changeOps(key, SelectionKey.OP_READ);
    }

    protected abstract void doWrite(Object pendingWrite, SelectionKey key) throws IOException;

    public void write(Object data, SelectionKey key) {
        var pendingWrites = this.channelToPendingWrites.get(key.channel());
        if (pendingWrites == null) {
            synchronized (this.channelToPendingWrites) {
                pendingWrites = this.channelToPendingWrites.get(key.channel());
                if (pendingWrites == null) {
                    pendingWrites = new ConcurrentLinkedQueue<>();
                    this.channelToPendingWrites.put(key.channel(), pendingWrites);
                }
            }
        }
        pendingWrites.add(data);
        reactor.changeOps(key, SelectionKey.OP_WRITE);
    }
}
