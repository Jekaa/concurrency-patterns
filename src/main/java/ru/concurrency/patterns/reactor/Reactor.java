package ru.concurrency.patterns.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Reactor {
    
    private final Dispatcher dispatcher;
    private final Selector selector;

    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private final ExecutorService reactorMain = Executors.newSingleThreadExecutor();
    
    public Reactor(Dispatcher dispatcher) throws IOException {
        this.dispatcher = dispatcher;
        this.selector = Selector.open();
    }
    
    public void start() {
        reactorMain.execute(() -> {
            try {
                System.out.println("Reactor started, waiting for events...");
                eventLoop();
            } catch (IOException ex) {
                System.out.println("exception in event loop" + ex.getMessage());
            }
        });
    }


    public void stop() throws InterruptedException, IOException {
        reactorMain.shutdown();
        selector.wakeup();
        if (!reactorMain.awaitTermination(4, TimeUnit.SECONDS)) {
            reactorMain.shutdownNow();
        }
        selector.close();
        System.out.println("Reactor stopped");
    }
    
    public Reactor registerChannel(AbstractNioChannel channel) throws IOException {
        var ops = channel.getInterestedOps();
        var key = channel.getJavaChannel().register(selector, ops);
        key.attach(channel);
        channel.setReactor(this);
        return this;
    }

    private void eventLoop() throws IOException {
        while (!Thread.interrupted()) {
            processPendingCommands();
            selector.select();
            
            var keys = selector.selectedKeys();
            var iterator = keys.iterator();

            while (iterator.hasNext()) {
                var key = iterator.next();
                if (!key.isValid()) {
                    iterator.remove();
                    continue;
                }
                processKey(key);
            }
            keys.clear();
        }
    }

    private void processPendingCommands() {
        var iterator = queue.iterator();
        while (iterator.hasNext()) {
            var command = iterator.next();
            command.run();
            iterator.remove();
        }
    }
    
    private void processKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            onChannelAcceptable(key);
        } else if (key.isReadable()) {
            onChannelReadable(key);
        } else if (key.isWritable()) {
            onChannelWritable(key);
        }
    }

    private static void onChannelWritable(SelectionKey key) throws IOException {
        var channel = (AbstractNioChannel) key.attachment();
        channel.flush(key);
    }

    private void onChannelReadable(SelectionKey key) {
        try {
            var readObject = ((AbstractNioChannel) key.attachment()).read(key);
            dispatchReadEvent(key, readObject);
        } catch (IOException e) {
            try {
                key.channel().close();
            } catch (IOException ex) {
                System.out.println("error closing channel" + ex);
            }
        }
    }

    private void dispatchReadEvent(SelectionKey key, Object readObject) {
        dispatcher.onChannelReadEvent((AbstractNioChannel) key.attachment(), readObject, key);
    }

    private void onChannelAcceptable(SelectionKey key) throws IOException {
        var serverSocketChannel = (ServerSocketChannel) key.channel();
        var socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        var readKey = socketChannel.register(selector, SelectionKey.OP_READ);
        readKey.attach(key.attachment());
    }

    public void changeOps(SelectionKey key, int interestedOps) {
        queue.add(new ChangeKeyOpsCommand(key, interestedOps));
        selector.wakeup();
    }

    record ChangeKeyOpsCommand(SelectionKey key, int interestedOps) implements Runnable {

        public void run() {
            key.interestOps(interestedOps);
        }

        @Override
        public String toString() {
            return "Change of ops to: " + interestedOps;
        }
    }
}
