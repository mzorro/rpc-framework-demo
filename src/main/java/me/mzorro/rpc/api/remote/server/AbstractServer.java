package me.mzorro.rpc.api.remote.server;

import me.mzorro.rpc.api.AbstractFuture;
import me.mzorro.rpc.api.remote.RequestHandler;

/**
 * Created On 05/02 2018
 *
 * @author mzorrox@gmail.com
 */
public abstract class AbstractServer extends AbstractFuture<Server.State> implements Server {

    protected int port;

    protected RequestHandler requestHandler;

    protected void open() {
        try {
            doOpen();
            setResult(State.ESTABLISHED);
        } catch (Throwable cause) {
            setResult(State.failed(cause));
            throw new RuntimeException("error open server on port:" + port, cause);
        }
    }

    public void close() {
        try {
            doClose();
            setResult(State.CLOSED);
        } catch (Throwable cause) {
            setResult(State.failed(cause));
            throw new RuntimeException("error close server on port:" + port, cause);
        }
    }

    protected abstract void doOpen() throws Throwable;

    protected abstract void doClose() throws Throwable;

    public AbstractServer(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
    }
}
