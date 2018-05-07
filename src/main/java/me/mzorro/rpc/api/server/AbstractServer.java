package me.mzorro.rpc.api.server;

/**
 * Created On 05/02 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public abstract class AbstractServer implements Server, Runnable {

    protected int port;

    protected RequestHandler requestHandler;

    protected volatile boolean closed = false;

    public void close() {
        this.closed = true;
    }

    public AbstractServer(int port, RequestHandler requestHandler) {
        this.port = port;
        this.requestHandler = requestHandler;
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
}
