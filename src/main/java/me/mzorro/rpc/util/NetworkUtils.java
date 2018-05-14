package me.mzorro.rpc.util;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.NetworkChannel;

/**
 * Created On 05/13 2018
 *
 * @author mzorrox@gmail.com
 */
public class NetworkUtils {

    @SuppressWarnings("unchecked")
    public static <T extends SocketAddress> T getLocalAddress(NetworkChannel channel) {
        try {
            return (T) channel.getLocalAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends SocketAddress> T getRemoteAddress(NetworkChannel channel) {
        try {
            return (T) channel.getLocalAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
