package me.mzorro.rpc.api.remote;

import me.mzorro.rpc.api.Response;
import me.mzorro.rpc.api.ResultFuture;

/**
 * Created On 04/04 2018
 *
 * @author mzorrox@gmail.com
 */
public interface ChannelReader extends ResultFuture<Response> {

    Object PARTIALLY_READ = new Object();

    Object read();
}
