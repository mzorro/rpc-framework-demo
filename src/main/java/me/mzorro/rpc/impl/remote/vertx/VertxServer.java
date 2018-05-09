package me.mzorro.rpc.impl.remote.vertx;

import java.io.IOException;

import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import me.mzorro.rpc.api.remote.RequestHandler;
import me.mzorro.rpc.api.remote.server.AbstractServer;

/**
 * Created On 05/02 2018
 *
 * @author mzorrox@gmail.com
 */
public class VertxServer extends AbstractServer {

    private NetServer server = null;

    public VertxServer(int port, RequestHandler requestHandler) {
        super(port, requestHandler);
        open();
    }

    @Override
    public void open() {
        server = VertxTransporter.vertx.createNetServer(new NetServerOptions()).connectHandler(socket -> {
            VertxChannel channel = new VertxChannel(socket, requestHandler);
            channel.read();
        }).listen(port, event -> {
            if (event.succeeded()) {
                setResult(State.ESTABLISHED);
            } else {
                setResult(State.failed(event.cause()));
            }
        });
    }

    @Override
    public void close() {
        if (server != null) {
            setResult(null);
            server.close(event -> {
                if (event.succeeded()) {
                    setResult(State.ESTABLISHED);
                } else {
                    setResult(State.failed(event.cause()));
                }
            });
            try {
                get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doOpen() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doClose() {
        throw new UnsupportedOperationException();
    }
}
