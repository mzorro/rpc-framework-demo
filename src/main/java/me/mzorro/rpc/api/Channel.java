package me.mzorro.rpc.api;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

/**
 * Created On 03/16 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public interface Channel extends Endpoint {

    InetSocketAddress getRemoteAddress();

    Future<Object> read();
}
