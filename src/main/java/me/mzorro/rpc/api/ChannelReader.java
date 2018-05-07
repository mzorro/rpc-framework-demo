package me.mzorro.rpc.api;

/**
 * Created On 04/04 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public interface ChannelReader {

    Object PARTIALLY_READ = new Object();

    Object read();
}
