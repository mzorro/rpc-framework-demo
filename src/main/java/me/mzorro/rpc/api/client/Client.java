package me.mzorro.rpc.api.client;

import java.util.concurrent.Future;

import me.mzorro.rpc.api.Channel;
import me.mzorro.rpc.api.Response;

/**
 * Created On 04/03 2018
 *
 * @author mzorrox@gmail.com
 */
public interface Client extends Channel {

    Future<Response> request(Object request);
}
