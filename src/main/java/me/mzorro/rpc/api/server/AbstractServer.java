package me.mzorro.rpc.api.server;

import java.io.IOException;

import me.mzorro.rpc.api.AbstractFuture;

/**
 * Created On 05/02 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public abstract class AbstractServer extends AbstractFuture<Object> implements Server, Runnable {

    protected int port;

    protected RequestHandler requestHandler;

    protected volatile boolean closed = false;

    public void close() {
        this.closed = true;
        try {
            doClose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void doClose() throws IOException;

    public AbstractServer(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
}
