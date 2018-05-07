package me.mzorro.rpc.impl.nio.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Future;

import me.mzorro.rpc.api.Channel;
import me.mzorro.rpc.api.Response;
import me.mzorro.rpc.api.ResponseFutureDelegate;
import me.mzorro.rpc.api.client.AbstractClient;
import me.mzorro.rpc.impl.nio.NIOChannel;

/**
 * Created On 04/04 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class NIOClient extends AbstractClient {

    private SocketChannel socket;

    private NIOChannel channel;

    public NIOClient(InetAddress address, int port) {
        InetSocketAddress socketAddress = new InetSocketAddress(address, port);
        try {
            socket = SocketChannel.open(socketAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        channel = new NIOChannel(socket, null);
    }

    @Override
    protected Channel getChannel() {
        return channel;
    }

    @Override
    public Future<Response> request(Object request) {
        try {
            send(request, true);
        } catch (IOException e) {
            throw new RuntimeException("send message error", e);
        }
        return new ResponseFutureDelegate(read());
    }

    @Override
    public void send(Object message, boolean sent) throws IOException {
        channel.send(message, sent);
    }

    @Override
    public Future<Object> read() {
        return channel.read();
    }
}
