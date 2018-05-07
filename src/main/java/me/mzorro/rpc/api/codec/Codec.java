package me.mzorro.rpc.api.codec;

import java.io.IOException;

/**
 * Created On 04/03 2018
 *
 * @author mzorrox@gmail.com
 */
public interface Codec {

    byte[] encode(Object message) throws IOException;

    Object decode(byte[] b) throws IOException, ClassNotFoundException;
}
