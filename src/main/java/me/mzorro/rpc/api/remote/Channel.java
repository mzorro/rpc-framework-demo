package me.mzorro.rpc.api.remote;

import java.net.InetSocketAddress;

import me.mzorro.rpc.api.Response;
import me.mzorro.rpc.api.ResultFuture;

/**
 * Created On 03/16 2018
 *
 * @author mzorrox@gmail.com
 */
public interface Channel extends Endpoint {

    InetSocketAddress getRemoteAddress();

    ResultFuture<Response> read();
}
