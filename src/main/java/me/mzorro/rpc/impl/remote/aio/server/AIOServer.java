package me.mzorro.rpc.impl.remote.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import me.mzorro.rpc.api.remote.RequestHandler;
import me.mzorro.rpc.api.remote.server.AbstractServer;
import me.mzorro.rpc.impl.remote.aio.AIOChannel;

/**
 * Created On 04/03 2018
 *
 * @author mzorrox@gmail.com
 */
public class AIOServer extends AbstractServer {

    private volatile AsynchronousServerSocketChannel server = null;

    public AIOServer(int port, RequestHandler requestHandler) {
        super(port, requestHandler);
        open();
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    protected void doOpen() throws IOException {
        server = AsynchronousServerSocketChannel.open();
        server.bind(new InetSocketAddress(port));
        server.accept(server, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {
            @Override
            public void completed(AsynchronousSocketChannel socket, AsynchronousServerSocketChannel server) {
                if (!isClosed() && server.isOpen()) {
                    new AIOChannel(socket, requestHandler).read();
                    server.accept(server, this);
                }
            }

            @Override
            public void failed(Throwable exc, AsynchronousServerSocketChannel server) {
                exc.printStackTrace();
            }
        });
    }

    @Override
    protected void doClose() throws IOException {
        if (server != null) {
            server.close();
        }
    }
}
