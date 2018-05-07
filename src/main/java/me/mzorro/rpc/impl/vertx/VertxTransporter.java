package me.mzorro.rpc.impl.vertx;

import java.net.InetAddress;

import io.vertx.core.Vertx;
import me.mzorro.rpc.api.Transporter;
import me.mzorro.rpc.api.client.Client;
import me.mzorro.rpc.api.server.RequestHandler;
import me.mzorro.rpc.api.server.Server;
import me.mzorro.rpc.impl.nio.client.NIOClient;

/**
 * Created On 05/02 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class VertxTransporter implements Transporter {

    static final Vertx vertx = Vertx.vertx();

    @Override
    public Client connect(InetAddress address, int port) {
        return new NIOClient(address, port);
    }

    @Override
    public Server listen(int port, RequestHandler requestHandler) {
        return new VertxServer(port, requestHandler);
    }
}
