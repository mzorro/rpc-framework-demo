package me.mzorro.rpc.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import me.mzorro.rpc.api.codec.Codec;

/**
 * Created On 04/03 2018
 *
 * @author mzorrox@gmail.com
 */
public class JavaSerializationCodec implements Codec {

    @Override
    public byte[] encode(Object message) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(256);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(message);
        oos.flush();
        return bos.toByteArray();
    }

    @Override
    public Object decode(byte[] b) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b));
        Object message = ois.readObject();
        ois.close();
        return message;
    }
}
