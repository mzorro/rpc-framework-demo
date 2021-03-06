package me.mzorro.rpc.api.remote;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.mzorro.rpc.api.AbstractFuture;
import me.mzorro.rpc.api.Response;
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
    public ResultFuture<Response> read() {
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

    protected abstract class AbstractReader extends AbstractFuture<Response> implements ChannelReader {

        protected void success(Object message) {
            setResult(Response.success(message));
            received(message);
        }

        protected void failed(Throwable cause) {
            setResult(Response.failed(cause));
        }
    }

    protected abstract class AbstractWriter extends AbstractFuture<ChannelWriter.State> implements ChannelWriter {

        protected void complete() {
            setResult(State.COMPLETED);
        }

        protected void failed(Throwable cause) {
            setResult(State.failed(cause));
        }
    }

    @Override
    public void close() throws IOException {
        doClose();
    }

    protected abstract void doClose() throws IOException;
}
