package me.mzorro.rpc.impl.vertx;

import io.vertx.core.net.NetServerOptions;
import me.mzorro.rpc.api.server.AbstractServer;
import me.mzorro.rpc.api.server.RequestHandler;

/**
 * Created On 05/02 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class VertxServer extends AbstractServer {

    public VertxServer(int port, RequestHandler requestHandler) {
        super(port, requestHandler);
    }

    @Override
    public void run() {
        VertxTransporter.vertx.createNetServer(new NetServerOptions()).connectHandler(socket -> {
            VertxChannel channel = new VertxChannel(socket, requestHandler);
            channel.read();
        }).listen(port);
    }
}
