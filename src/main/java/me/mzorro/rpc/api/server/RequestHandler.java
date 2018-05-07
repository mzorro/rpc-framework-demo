package me.mzorro.rpc.api.server;

/**
 * Created On 03/15 2018
 *
 * @author mzorrox@gmail.com
 */
public interface RequestHandler {

    Object accept(Object message);
}
