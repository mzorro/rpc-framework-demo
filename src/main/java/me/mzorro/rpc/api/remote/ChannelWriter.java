package me.mzorro.rpc.api.remote;

import me.mzorro.rpc.api.ResultFuture;

/**
 * Created On 04/04 2018
 *
 * @author mzorrox@gmail.com
 */
public interface ChannelWriter extends ResultFuture<Object> {

    /**
     * @return 是否写完
     */
    boolean write();
}
