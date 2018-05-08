package me.mzorro.rpc.api.remote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.NetworkChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.mzorro.rpc.api.AbstractFuture;
import me.mzorro.rpc.api.ResultFuture;

/**
 * Created On 04/20 2018
 *
 * @author mzorrox@gmail.com
 */
public abstract class AbstractChannel implements Channel {

    private static final Logger logger = LoggerFactory.getLogger(AbstractChannel.class);

    private final RequestHandler requestHandler;

    protected AbstractChannel(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    protected abstract NetworkChannel getNetworkChannel();

    @Override
    public InetSocketAddress getLocalAddress() {
        try {
            return (InetSocketAddress) getNetworkChannel().getLocalAddress();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        try {
            return (InetSocketAddress) getNetworkChannel().getLocalAddress();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void logRead(int bytes) {
        logger.info(this + " read " + bytes + " bytes from " + getRemoteAddress());
    }

    public void logWrite(int bytes) {
        logger.info(this + " write " + bytes + " bytes to " + getRemoteAddress());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        sb.append('(').append(getLocalAddress()).append(')');
        return sb.toString();
    }

    @Override
    public ResultFuture<Object> read() {
        AbstractReader reader = newReader();
        reader.read();
        return reader;
    }

    @Override
    public void send(Object message, boolean sent) throws IOException {
        AbstractWriter writer = newWriter(message);
        if (!writer.write() && sent) {
            try {
                writer.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void received(Object message) {
        if (requestHandler == null) {
            return;
        }

        Object result;
        try {
            result = requestHandler.accept(message);
        } catch (Throwable throwable) {
            result = throwable;
        }

        try {
            send(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract AbstractReader newReader();

    public abstract AbstractWriter newWriter(Object message) throws IOException;

    protected abstract class AbstractReader extends AbstractFuture<Object> implements ChannelReader {

        @Override
        protected void setResult(Object result) {
            super.setResult(result);
            received(result);
        }
    }

    protected abstract class AbstractWriter extends AbstractFuture<Object> implements ChannelWriter {
    }
}
