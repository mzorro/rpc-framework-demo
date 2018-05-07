package me.mzorro.rpc.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SocketChannel;

import me.mzorro.rpc.api.AbstractChannel;
import me.mzorro.rpc.api.codec.Codec;
import me.mzorro.rpc.api.server.RequestHandler;
import me.mzorro.rpc.codec.JavaSerializationCodec;

/**
 * Created On 04/03 2018
 *
 * @author mzorrox@gmail.com
 */
public class NIOChannel extends AbstractChannel {

    private static final Codec codec = new JavaSerializationCodec();

    final SocketChannel socket;

    public NIOChannel(SocketChannel socket, RequestHandler requestHandler) {
        super(requestHandler);
        this.socket = socket;
    }

    @Override
    protected NetworkChannel getNetworkChannel() {
        return socket;
    }

    public Reader newReader() {
        return new Reader();
    }

    public Writer newWriter(Object message) throws IOException {
        byte[] body = codec.encode(message);
        return new Writer(body);
    }

    private class Reader extends AbstractReader {

        protected final ByteBuffer headerBuffer = ByteBuffer.allocate(4);

        protected ByteBuffer bodyBuffer;

        protected int bodyLength = -1;

        @Override
        public Object read() {
            int readBytes = 0;
            try {
                if (bodyLength == -1) {
                    readBytes += socket.read(headerBuffer);
                    if (headerBuffer.hasRemaining()) {
                        return PARTIALLY_READ;//part read
                    }
                    headerBuffer.flip();
                    bodyLength = headerBuffer.getInt();
                    if (bodyLength <= 0) {
                        throw new IllegalStateException("wrong data read");
                    }
                    bodyBuffer = ByteBuffer.allocate(bodyLength);
                }

                readBytes += socket.read(bodyBuffer);
                if (bodyBuffer.hasRemaining()) {
                    return PARTIALLY_READ;//part read
                }
                Object message = codec.decode(bodyBuffer.array());
                setResult(message);
                return message;
            } catch (Exception e) {
                setResult(e);
                return null;
            } finally {
                logRead(readBytes);
            }
        }
    }

    private class Writer extends AbstractWriter {

        protected final ByteBuffer headerBuffer = ByteBuffer.allocate(4);

        protected final ByteBuffer bodyBuffer;

        protected Writer(byte[] body) {
            headerBuffer.putInt(body.length);
            headerBuffer.flip();
            this.bodyBuffer = ByteBuffer.wrap(body);
        }

        @Override
        public boolean write() {
            int writeBytes = 0;
            try {
                if (headerBuffer.hasRemaining()) {
                    writeBytes += socket.write(headerBuffer);
                    if (headerBuffer.hasRemaining()) {
                        return false;
                    }
                }
                if (bodyBuffer.hasRemaining()) {
                    writeBytes += socket.write(bodyBuffer);
                    if (bodyBuffer.hasRemaining()) {
                        return false;
                    }
                }
                setResult(true);
                return true;
            } catch (IOException e) {
                setResult(false);
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                logWrite(writeBytes);
            }
        }
    }
}
