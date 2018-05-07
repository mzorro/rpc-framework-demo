package me.mzorro.rpc.api;

import java.net.InetAddress;

import me.mzorro.rpc.api.client.Client;
import me.mzorro.rpc.api.server.RequestHandler;
import me.mzorro.rpc.api.server.Server;

/**
 * Created On 05/02 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public interface Transporter {

    Client connect(InetAddress address, int port);

    Server listen(int port, RequestHandler requestHandler);
}
