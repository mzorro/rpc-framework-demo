package me.mzorro.rpc.api.remote;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created On 04/20 2018
 *
 * @author mzorrox@gmail.com
 */
public interface Endpoint extends Closeable {

    InetSocketAddress getLocalAddress();

    /**
     * 发送消息到对端
     *
     * @param message 消息对象
     * @param sent    是否等待发送完成后返回
     */
    void send(Object message, boolean sent) throws IOException;

    default void send(Object message) throws IOException {
        send(message, false);
    }
}
