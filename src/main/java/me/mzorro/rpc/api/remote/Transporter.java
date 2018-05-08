package me.mzorro.rpc.api.remote;

import java.net.InetAddress;

import me.mzorro.rpc.api.remote.client.Client;
import me.mzorro.rpc.api.remote.server.Server;

/**
 * Created On 05/02 2018
 *
 * @author mzorrox@gmail.com
 */
public interface Transporter {

    Client connect(InetAddress address, int port);

    Server listen(int port, RequestHandler requestHandler);
}
