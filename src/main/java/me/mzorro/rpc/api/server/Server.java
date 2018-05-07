package me.mzorro.rpc.api.server;

import java.util.concurrent.Future;

/**
 * Created On 03/15 2018
 *
 * @author mzorrox@gmail.com
 */
public interface Server extends Future<Object> {

    void close();
}
