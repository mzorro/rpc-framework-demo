package me.mzorro.rpc.impl.remote.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.mzorro.rpc.api.remote.Channel;
import me.mzorro.rpc.api.remote.ChannelReader;
import me.mzorro.rpc.api.remote.ChannelWriter;
import me.mzorro.rpc.api.remote.RequestHandler;
import me.mzorro.rpc.api.remote.server.AbstractServer;
import me.mzorro.rpc.impl.remote.nio.NIOChannel;

/**
 * Created On 03/15 2018
 *
 * @author mzorrox@gmail.com
 */
public class NIOServer extends AbstractServer implements Runnable {

    private final Map<Channel, ChannelWriter> writers = new ConcurrentHashMap<>();

    private final Map<Channel, ChannelReader> readers = new ConcurrentHashMap<>();

    private volatile ServerSocketChannel socket = null;

    private volatile Selector selector = null;

    public NIOServer(int port, RequestHandler requestHandler) {
        super(port, requestHandler);
        open();
    }

    @Override
    protected void doOpen() throws IOException {
        socket = ServerSocketChannel.open();
        socket.configureBlocking(false);
        selector = Selector.open();
        socket.bind(new InetSocketAddress(port));
        socket.register(selector, SelectionKey.OP_ACCEPT);
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    protected void doClose() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }

    @Override
    public void run() {
        try {
            while (!isClosed() && socket.isOpen()) {
                if (selector.select() == 0) {
                    continue;
                }
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (!isClosed() && socket.isOpen() && it.hasNext()) {
                    SelectionKey key = it.next();
                    try {
                        if (key.isAcceptable()) {
                            SocketChannel clientSocket = socket.accept();
                            clientSocket.configureBlocking(false);
                            NIOChannel channel = new NIOChannel(clientSocket, requestHandler);
                            readers.put(channel, channel.newReader());
                            clientSocket.register(selector, SelectionKey.OP_READ, channel);
                        } else if (key.isReadable()) {
                            NIOChannel channel = (NIOChannel) key.attachment();
                            ChannelReader reader = readers.get(channel);
                            Object message = reader.read();
                            if (message != ChannelReader.PARTIALLY_READ) {
                                readers.remove(channel);
                                Object result = requestHandler.accept(message);
                                ChannelWriter writer = channel.newWriter(result);
                                writers.put(channel, writer);
                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                        } else if (key.isWritable()) {
                            Channel channel = (Channel) key.attachment();
                            ChannelWriter writer = writers.get(channel);
                            if (writer.write()) {
                                writers.remove(channel);
                                key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        it.remove();
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            close();
        }
    }

    private class Accepter implements Runnable {

        @Override
        public void run() {
        }
    }
}
