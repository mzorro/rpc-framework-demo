package me.mzorro.rpc.impl.remote.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.NetworkChannel;

import me.mzorro.rpc.api.codec.Codec;
import me.mzorro.rpc.api.remote.RequestHandler;
import me.mzorro.rpc.impl.codec.JavaSerializationCodec;
import me.mzorro.rpc.api.remote.AbstractChannel;

/**
 * Created On 04/06 2018
 *
 * @author mzorrox@gmail.com
 */
public class AIOChannel extends AbstractChannel {

    private static final Codec codec = new JavaSerializationCodec();

    final AsynchronousSocketChannel socket;

    final RequestHandler requestHandler;

    public AIOChannel(AsynchronousSocketChannel socket, RequestHandler requestHandler) {
        super(requestHandler);
        this.socket = socket;
        this.requestHandler = requestHandler;
    }

    @Override
    protected NetworkChannel getNetworkChannel() {
        return socket;
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
    public void send(Object message, boolean sent) throws IOException {
        byte[] body = codec.encode(message);
        new Writer(body).write();
    }

    private class Reader extends AbstractReader {

        protected final ByteBuffer headerBuffer = ByteBuffer.allocate(4);

        protected ByteBuffer bodyBuffer;

        protected int bodyLength = -1;

        @Override
        public Object read() {
            socket.read(headerBuffer, this, new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(Integer result, Object attachment) {
                    logRead(result);
                    if (bodyLength == -1) {
                        if (headerBuffer.hasRemaining()) {
                            socket.read(headerBuffer, attachment, this);
                        } else {
                            headerBuffer.flip();
                            bodyLength = headerBuffer.getInt();
                            if (bodyLength <= 0) {
                                throw new IllegalStateException("wrong data read");
                            }
                            bodyBuffer = ByteBuffer.allocate(bodyLength);
                            socket.read(bodyBuffer, attachment, this);
                        }
                    } else if (bodyBuffer.hasRemaining()) {
                        socket.read(bodyBuffer, attachment, this);
                    } else {
                        try {
                            Object message = codec.decode(bodyBuffer.array());
                            setResult(message);
                        } catch (Exception e) {
                            setResult(e);
                        }
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    setResult(exc);
                }
            });
            return null;
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
            socket.write(headerBuffer, this, new CompletionHandler<Integer, Writer>() {
                @Override
                public void completed(Integer result, Writer attachment) {
                    logWrite(result);
                    if (headerBuffer.hasRemaining()) {
                        socket.write(headerBuffer, attachment, this);
                    } else if (bodyBuffer.hasRemaining()) {
                        socket.write(bodyBuffer, attachment, this);
                    } else {
                        setResult(true);
                    }
                }

                @Override
                public void failed(Throwable exc, Writer attachment) {
                    exc.printStackTrace();
                    setResult(false);
                }
            });
            return false;
        }
    }
}
