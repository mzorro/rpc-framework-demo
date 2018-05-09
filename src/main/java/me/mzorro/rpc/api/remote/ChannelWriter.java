package me.mzorro.rpc.api.remote;

import java.util.Objects;

import me.mzorro.rpc.api.Cause;
import me.mzorro.rpc.api.ResultFuture;

/**
 * Created On 04/04 2018
 *
 * @author mzorrox@gmail.com
 */
public interface ChannelWriter extends ResultFuture<ChannelWriter.State> {

    /**
     * @return 是否写完
     */
    boolean write();

    class State implements Cause {

        @Override
        public Throwable getCause() {
            return null;
        }

        public static final State COMPLETED = new State();

        public static State failed(Throwable t) {
            return new FailedState(Objects.requireNonNull(t));
        }

        private static class FailedState extends State {

            private final Throwable cause;

            @Override
            public Throwable getCause() {
                return cause;
            }

            private FailedState(Throwable cause) {
                this.cause = cause;
            }
        }
    }
}
