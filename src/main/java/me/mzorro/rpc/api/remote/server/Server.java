package me.mzorro.rpc.api.remote.server;

import java.io.Closeable;
import java.util.Objects;

import me.mzorro.rpc.api.Cause;
import me.mzorro.rpc.api.ResultFuture;

/**
 * Created On 03/15 2018
 *
 * @author mzorrox@gmail.com
 */
public interface Server extends ResultFuture<Server.State>, Closeable {

    default boolean isEstablished() {
        return getNow() == State.ESTABLISHED;
    }

    default boolean isClosed() {
        return getNow() == State.CLOSED;
    }

    class State implements Cause {

        @Override
        public Throwable getCause() {
            return null;
        }

        public static State ESTABLISHED = new State();

        public static State CLOSED = new State();

        public static State failed(Throwable cause) {
            return new FailedState(Objects.requireNonNull(cause));
        }

        private static class FailedState extends State {
            private final Throwable cause;

            @Override
            public Throwable getCause() {
                return cause;
            }

            public FailedState(Throwable cause) {
                this.cause = cause;
            }
        }
    }
}
