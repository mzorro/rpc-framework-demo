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

    private AsynchronousServerSocketChannel server = null;

    public AIOServer(int port, RequestHandler requestHandler) {
        super(port, requestHandler);
    }

    @Override
    public void run() {
        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            server.accept(server, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel socket, Object attachment) {
                    if (!closed && server.isOpen()) {
                        new AIOChannel(socket, requestHandler).read();
                        server.accept(attachment, this);
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    exc.printStackTrace();
                }
            });
            setResult(true);
        } catch (IOException e) {
            setResult(e);
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    protected void doClose() throws IOException {
        if (server != null) {
            server.close();
        }
    }
}
