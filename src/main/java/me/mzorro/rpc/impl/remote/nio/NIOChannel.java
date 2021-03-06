package me.mzorro.rpc.impl.remote.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import me.mzorro.rpc.api.codec.Codec;
import me.mzorro.rpc.api.remote.AbstractChannel;
import me.mzorro.rpc.api.remote.RequestHandler;
import me.mzorro.rpc.impl.codec.JavaSerializationCodec;
import me.mzorro.rpc.util.NetworkUtils;

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

    public Reader newReader() {
        return new Reader();
    }

    public Writer newWriter(Object message) throws IOException {
        byte[] body = codec.encode(message);
        return new Writer(body);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return NetworkUtils.getRemoteAddress(socket);
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return NetworkUtils.getLocalAddress(socket);
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
                success(message);
                return message;
            } catch (Throwable t) {
                failed(t);
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
                complete();
                return true;
            } catch (Throwable cause) {
                failed(cause);
                throw new RuntimeException(cause);
            } finally {
                logWrite(writeBytes);
            }
        }
    }

    @Override
    protected void doClose() throws IOException {
        if (socket != null && socket.isOpen()) {
            socket.close();
        }
    }
}
