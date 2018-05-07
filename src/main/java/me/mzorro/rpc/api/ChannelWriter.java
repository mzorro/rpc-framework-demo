package me.mzorro.rpc.api;

/**
 * Created On 04/04 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public interface ChannelWriter {

    /**
     * @return 是否写完
     */
    boolean write();
}
