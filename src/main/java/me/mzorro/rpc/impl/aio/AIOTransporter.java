package me.mzorro.rpc.impl.aio;

import java.net.InetAddress;

import me.mzorro.rpc.api.Transporter;
import me.mzorro.rpc.api.client.Client;
import me.mzorro.rpc.api.server.RequestHandler;
import me.mzorro.rpc.api.server.Server;
import me.mzorro.rpc.impl.aio.server.AIOServer;
import me.mzorro.rpc.impl.nio.client.NIOClient;

/**
 * Created On 05/02 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class AIOTransporter implements Transporter {

    @Override
    public Client connect(InetAddress address, int port) {
        return new NIOClient(address, port);
    }

    @Override
    public Server listen(int port, RequestHandler requestHandler) {
        return new AIOServer(port, requestHandler);
    }
}
