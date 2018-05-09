package me.mzorro.rpc.impl.remote.nio.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Future;

import me.mzorro.rpc.api.Response;
import me.mzorro.rpc.api.ResultFuture;
import me.mzorro.rpc.api.remote.Channel;
import me.mzorro.rpc.api.remote.client.AbstractClient;
import me.mzorro.rpc.impl.remote.nio.NIOChannel;

/**
 * Created On 04/04 2018
 *
 * @author mzorrox@gmail.com
 */
public class NIOClient extends AbstractClient {

    private final SocketChannel socket;

    private final NIOChannel channel;

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
        return read();
    }

    @Override
    public void disconnect() throws IOException {
        socket.close();
    }

    @Override
    public void send(Object message, boolean sent) throws IOException {
        channel.send(message, sent);
    }

    @Override
    public ResultFuture<Response> read() {
        return channel.read();
    }
}
