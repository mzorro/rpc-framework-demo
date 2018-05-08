package me.mzorro.rpc.api.remote.client;

import java.net.InetSocketAddress;

import me.mzorro.rpc.api.remote.Channel;

/**
 * Created On 04/20 2018
 *
 * @author mzorrox@gmail.com
 */
public abstract class AbstractClient implements Client {

    protected abstract Channel getChannel();

    @Override
    public InetSocketAddress getLocalAddress() {
        return getChannel().getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return getChannel().getRemoteAddress();
    }
}
