package me.mzorro.rpc.impl.remote.vertx;

import java.net.InetAddress;

import io.vertx.core.Vertx;
import me.mzorro.rpc.api.remote.Transporter;
import me.mzorro.rpc.api.remote.client.Client;
import me.mzorro.rpc.api.remote.RequestHandler;
import me.mzorro.rpc.api.remote.server.Server;
import me.mzorro.rpc.impl.remote.nio.client.NIOClient;

/**
 * Created On 05/02 2018
 *
 * @author mzorrox@gmail.com
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
