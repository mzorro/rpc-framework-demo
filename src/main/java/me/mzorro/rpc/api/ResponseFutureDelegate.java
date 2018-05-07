package me.mzorro.rpc.api;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created On 05/06 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class ResponseFutureDelegate implements Future<Response> {

    private final Future<Object> future;

    public ResponseFutureDelegate(Future<Object> future) {
        this.future = future;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public Response get() throws InterruptedException, ExecutionException {
        return toResponse(future.get());
    }

    @Override
    public Response get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return toResponse(future.get(timeout, unit));
    }

    private Response toResponse(Object message) {
        Response response = new Response();
        if (message instanceof Throwable) {
            response.setThrowable((Throwable) message);
        } else {
            response.setResponse(message);
        }
        return response;
    }
}
