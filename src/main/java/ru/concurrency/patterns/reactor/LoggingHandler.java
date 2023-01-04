package ru.concurrency.patterns.reactor;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class LoggingHandler implements ChannelHandler {

    private static final byte[] ACK = "Data logged successfully".getBytes();

    @Override
    public void handle(AbstractNioChannel channel, Object readObject, SelectionKey key) {

        if (readObject instanceof ByteBuffer) {
            doLogging((ByteBuffer) readObject);
            sendReply(channel, key);
        } else if (readObject instanceof NioDatagramChannel.DatagramPacket) {
            var datagram = (NioDatagramChannel.DatagramPacket) readObject;
            doLogging(datagram.getData());
            sendReply(channel, datagram, key);
        } else {
            throw new IllegalStateException("Unknown data received");
        }
    }

    private static void sendReply(AbstractNioChannel channel,
                                  NioDatagramChannel.DatagramPacket incomingPacket,
                                  SelectionKey key) {
        var replyPacket = new NioDatagramChannel.DatagramPacket(ByteBuffer.wrap(ACK));
        replyPacket.setReceiver(incomingPacket.getSender());
        channel.write(replyPacket, key);
    }

    private static void sendReply(AbstractNioChannel channel, SelectionKey key) {
        var buffer = ByteBuffer.wrap(ACK);
        channel.write(buffer, key);
    }

    private static void doLogging(ByteBuffer data) {
        System.out.println(new String(data.array(), 0, data.limit()));
    }
}
