package me.mzorro.rpc.api;

/**
 * Created On 05/09 2018
 *
 * @author mzorrox@gmail.com
 */
public interface Cause {

    Throwable getCause();

    default void recreate() throws Throwable {
        if (getCause() != null) {
            throw getCause();
        }
    }
}
