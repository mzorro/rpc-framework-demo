package me.mzorro.rpc.api.remote.server;

import me.mzorro.rpc.api.ResultFuture;

/**
 * Created On 03/15 2018
 *
 * @author mzorrox@gmail.com
 */
public interface Server extends ResultFuture<Object> {

    void close();
}
