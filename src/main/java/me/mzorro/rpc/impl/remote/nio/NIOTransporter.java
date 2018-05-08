package me.mzorro.rpc.impl.remote.nio;

import java.net.InetAddress;

import me.mzorro.rpc.api.remote.Transporter;
import me.mzorro.rpc.api.remote.client.Client;
import me.mzorro.rpc.api.remote.RequestHandler;
import me.mzorro.rpc.api.remote.server.Server;
import me.mzorro.rpc.impl.remote.nio.client.NIOClient;
import me.mzorro.rpc.impl.remote.nio.server.NIOServer;

/**
 * Created On 05/02 2018
 *
 * @author mzorrox@gmail.com
 */
public class NIOTransporter implements Transporter {

    @Override
    public Client connect(InetAddress address, int port) {
        return new NIOClient(address, port);
    }

    @Override
    public Server listen(int port, RequestHandler requestHandler) {
        return new NIOServer(port, requestHandler);
    }
}
