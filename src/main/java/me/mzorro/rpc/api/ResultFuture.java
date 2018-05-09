package me.mzorro.rpc.api;

import java.util.concurrent.Future;

/**
 * Created On 05/08 2018
 *
 * @author mzorrox@gmail.com
 */
public interface ResultFuture<T> extends Future<T> {

    T getNow();
}
