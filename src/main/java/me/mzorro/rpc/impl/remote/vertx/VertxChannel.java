package me.mzorro.rpc.impl.remote.vertx;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.NetworkChannel;

import io.netty.buffer.ByteBuf;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.NetSocketInternal;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;
import me.mzorro.rpc.api.codec.Codec;
import me.mzorro.rpc.api.remote.AbstractChannel;
import me.mzorro.rpc.api.remote.RequestHandler;
import me.mzorro.rpc.impl.codec.JavaSerializationCodec;

/**
 * Created On 05/02 2018
 *
 * @author mzorrox@gmail.com
 */
public class VertxChannel extends AbstractChannel {

    private static final Codec codec = new JavaSerializationCodec();

    private final NetSocket socket;

    public VertxChannel(NetSocket socket, RequestHandler requestHandler) {
        super(requestHandler);
        this.socket = socket;
    }

    private InetSocketAddress unresolved(SocketAddress socketAddress) {
        return InetSocketAddress.createUnresolved(socketAddress.host(), socketAddress.port());
    }

    @Override
    protected NetworkChannel getNetworkChannel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return unresolved(socket.remoteAddress());
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return unresolved(socket.localAddress());
    }

    @Override
    public Reader newReader() {
        return new Reader();
    }

    @Override
    public Writer newWriter(Object message) throws IOException {
        byte[] body = codec.encode(message);
        return new Writer(body);
    }

    @Override
    protected void doClose() {
        if (socket != null) {
            socket.close();
        }
    }

    private class Reader extends AbstractReader implements Handler<Buffer> {

        protected final ByteBuffer headerBuffer = ByteBuffer.allocate(4);

        protected ByteBuffer bodyBuffer;

        protected int bodyLength = -1;

        @Override
        public void handle(Buffer buf) {
            byte[] data = buf.getBytes();
            int readBytes = 0;
            try {
                if (bodyLength == -1) {
                    int toRead = headerBuffer.remaining();
                    if (data.length >= toRead) {
                        headerBuffer.put(data, readBytes, toRead);
                        readBytes += toRead;
                        headerBuffer.flip();
                        bodyLength = headerBuffer.getInt();
                        bodyBuffer = ByteBuffer.allocate(bodyLength);
                    } else {
                        headerBuffer.put(buf.getBytes());
                    }
                }
                if (bodyLength > 0) {
                    int toRead = bodyBuffer.remaining();
                    bodyBuffer.put(data, readBytes, toRead);
                    readBytes += toRead;
                    if (bodyBuffer.hasRemaining()) {
                        return;
                    }
                    Object message = codec.decode(bodyBuffer.array());
                    success(message);
                }
            } catch (Throwable t) {
                failed(t);
            } finally {
                logRead(readBytes);
            }
        }

        @Override
        public Object read() {
            socket.handler(this);
            return null;
        }
    }

    private class Writer extends AbstractWriter {

        private final byte[] body;

        private Writer(byte[] body) {
            this.body = body;
        }

        @Override
        public boolean write() {
            ByteBuf byteBuf = Buffer.buffer().appendInt(body.length).appendBytes(body).getByteBuf();
            int writeBytes = byteBuf.readableBytes();
            ((NetSocketInternal) socket).writeMessage(byteBuf, event -> {
                if (event.succeeded()) {
                    complete();
                    logWrite(writeBytes);
                } else {
                    failed(event.cause());
                }
            });
            return false;
        }
    }
}
