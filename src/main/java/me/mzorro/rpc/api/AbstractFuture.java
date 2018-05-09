package me.mzorro.rpc.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created On 05/02 2018
 *
 * @author mzorrox@gmail.com
 */
public abstract class AbstractFuture<T> implements ResultFuture<T> {

    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    private volatile T result;

    protected void setResult(T result) {
        try {
            lock.lock();
            this.result = result;
            if (isDone()) {
                condition.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T getNow() {
        return result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return result != null;
    }

    @Override
    public T get() throws InterruptedException {
        if (!isDone()) {
            try {
                lock.lock();
                while (!isDone()) {
                    condition.await();
                }
            } finally {
                lock.unlock();
            }
        }

        return result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        if (timeout <= 0) {
            return get();
        }
        if (!isDone()) {
            try {
                long waitNanoseconds = unit.toNanos(timeout);
                lock.lock();
                while (!isDone()) {
                    if (waitNanoseconds <= 0) {
                        throw new TimeoutException();
                    }
                    long t = System.nanoTime();
                    if (condition.await(waitNanoseconds, TimeUnit.NANOSECONDS)) {
                        waitNanoseconds -= (System.nanoTime() - t);
                    } else {
                        waitNanoseconds = 0;
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return result;
    }
}
