package me.mzorro.rpc.impl.nio.server;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.mzorro.rpc.api.Channel;
import me.mzorro.rpc.api.ChannelReader;
import me.mzorro.rpc.api.ChannelWriter;
import me.mzorro.rpc.api.server.RequestHandler;
import me.mzorro.rpc.api.server.Server;
import me.mzorro.rpc.impl.nio.NIOChannel;

/**
 * Created On 03/15 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class NIOServer implements Server, Runnable {

    private final Map<Channel, ChannelWriter> writers = new ConcurrentHashMap<>();

    private final Map<Channel, ChannelReader> readers = new ConcurrentHashMap<>();

    private int port;

    private RequestHandler requestHandler;

    private volatile boolean closed = false;

    public void close() {
        this.closed = true;
    }

    public NIOServer(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        try {
            ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            Selector selector = Selector.open();
            server.bind(new InetSocketAddress(port));
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (!closed) {
                if (selector.select() == 0) {
                    continue;
                }
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (!closed && it.hasNext()) {
                    SelectionKey key = it.next();
                    try {
                        if (key.isAcceptable()) {
                            SocketChannel socket = server.accept();
                            socket.configureBlocking(false);
                            NIOChannel channel = new NIOChannel(socket, requestHandler);
                            readers.put(channel, channel.newReader());
                            socket.register(selector, SelectionKey.OP_READ, channel);
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
        }
    }
}
