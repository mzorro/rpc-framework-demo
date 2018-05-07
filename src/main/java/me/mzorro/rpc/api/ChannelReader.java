package me.mzorro.rpc.api;

/**
 * Created On 04/04 2018
 *
 * @author mzorrox@gmail.com
 */
public interface ChannelReader {

    Object PARTIALLY_READ = new Object();

    Object read();
}
