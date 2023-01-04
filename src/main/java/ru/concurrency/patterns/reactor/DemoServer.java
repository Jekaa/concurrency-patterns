package ru.concurrency.patterns.reactor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DemoServer {

    private Reactor reactor;
    private final List<AbstractNioChannel> channels = new ArrayList<>();
    private final Dispatcher dispatcher;

    public DemoServer(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public static void main(String[] args) throws IOException {
        new DemoServer(new ForkJoinPoolDispatcher(2)).start();
    }

    public void start() throws IOException {

        reactor = new Reactor(dispatcher);

        var loggingHandler = new LoggingHandler();

        reactor.registerChannel(tcpChannel(16666, loggingHandler))
                .registerChannel(tcpChannel(16667, loggingHandler))
                .registerChannel(udpChannel(16668, loggingHandler))
                .registerChannel(udpChannel(16669, loggingHandler))
                .start();
    }

    public void stop() throws InterruptedException, IOException {
        reactor.stop();
        dispatcher.stop();
        for (var channel : channels) {
            channel.getJavaChannel().close();
        }
    }

    private AbstractNioChannel tcpChannel(int port, ChannelHandler handler) throws IOException {
        var channel = new NioServerSocketChannel(port, handler);
        channel.bind();
        channels.add(channel);
        return channel;
    }

    private AbstractNioChannel udpChannel(int port, ChannelHandler handler) throws IOException {
        var channel = new NioDatagramChannel(port, handler);
        channel.bind();
        channels.add(channel);
        return channel;
    }
}
