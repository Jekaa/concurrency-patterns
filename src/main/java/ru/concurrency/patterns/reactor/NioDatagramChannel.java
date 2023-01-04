package ru.concurrency.patterns.reactor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class NioDatagramChannel extends AbstractNioChannel {

    private final int port;

    public NioDatagramChannel(int port, ChannelHandler handler) throws IOException {
        super(handler, DatagramChannel.open());
        this.port = port;
    }

    @Override
    public int getInterestedOps() {
        return SelectionKey.OP_READ;
    }

    @Override
    public DatagramPacket read(SelectionKey key) throws IOException {
        var buffer = ByteBuffer.allocate(1024);
        var sender = ((DatagramChannel) key.channel()).receive(buffer);

        buffer.flip();
        var packet = new DatagramPacket(buffer);
        packet.setSender(sender);

        return packet;
    }

    @Override
    public DatagramChannel getJavaChannel() {
        return (DatagramChannel) super.getJavaChannel();
    }

    @Override
    public void bind() throws IOException {
        getJavaChannel().socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
        getJavaChannel().configureBlocking(false);
        System.out.println("Bound UDP socket at port: " + port);
    }

    @Override
    protected void doWrite(Object pendingWrite, SelectionKey key) throws IOException {
        var pendingPacket = (DatagramPacket) pendingWrite;
        getJavaChannel().send(pendingPacket.getData(), pendingPacket.getReceiver());
    }

    @Override
    public void write(Object data, SelectionKey key) {
        super.write(data, key);
    }

    public static class DatagramPacket {
        private SocketAddress sender;
        private final ByteBuffer data;
        private SocketAddress receiver;

        public DatagramPacket(ByteBuffer data) {
            this.data = data;
        }

        public SocketAddress getSender() {
            return sender;
        }

        public void setSender(SocketAddress sender) {
            this.sender = sender;
        }

        public SocketAddress getReceiver() {
            return receiver;
        }

        public void setReceiver(SocketAddress receiver) {
            this.receiver = receiver;
        }

        public ByteBuffer getData() {
            return data;
        }
    }
}
