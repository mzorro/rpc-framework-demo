package me.mzorro.rpc.impl.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import me.mzorro.rpc.api.server.AbstractServer;
import me.mzorro.rpc.api.server.RequestHandler;
import me.mzorro.rpc.impl.aio.AIOChannel;

/**
 * Created On 04/03 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class AIOServer extends AbstractServer {

    public AIOServer(int port, RequestHandler requestHandler) {
        super(port, requestHandler);
    }

    @Override
    public void run() {
        try {
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            server.accept(server, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                @Override
                public void completed(AsynchronousSocketChannel socket, Object attachment) {
                    new AIOChannel(socket, requestHandler).read();
                    server.accept(attachment, this);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    exc.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
