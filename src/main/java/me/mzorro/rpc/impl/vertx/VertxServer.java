package me.mzorro.rpc.impl.vertx;

import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import me.mzorro.rpc.api.server.AbstractServer;
import me.mzorro.rpc.api.server.RequestHandler;

/**
 * Created On 05/02 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class VertxServer extends AbstractServer {

    private NetServer server = null;

    @Override
    protected void doClose() {
        if (server != null) {
            setResult(null);
            server.close(event -> {
                if (event.succeeded()) {
                    setResult(true);
                } else {
                    setResult(event.cause());
                }
            });
            try {
                get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public VertxServer(int port, RequestHandler requestHandler) {
        super(port, requestHandler);
    }

    @Override
    public void run() {
        server = VertxTransporter.vertx.createNetServer(new NetServerOptions()).connectHandler(socket -> {
            VertxChannel channel = new VertxChannel(socket, requestHandler);
            channel.read();
        }).listen(port, event -> {
            if (event.succeeded()) {
                setResult(true);
            } else {
                setResult(event.cause());
            }
        });
    }
}
