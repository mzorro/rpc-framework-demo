package me.mzorro.rpc.api;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created On 05/08 2018
 *
 * @author mzorrox@gmail.com
 */
public interface ResultFuture<T> extends Future<T> {

    T recreate() throws Throwable;

    T recreate(long timeout, TimeUnit unit) throws Throwable;
}
