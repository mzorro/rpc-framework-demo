package me.mzorro.rpc.api;

/**
 * Created On 05/02 2018
 *
 * @author mzorrox@gmail.com
 */
public class Response implements Cause {

    private final Object message;

    private final Throwable cause;

    public Response(Object message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    public Object getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

    public Object recreateAndGetMessage() throws Throwable {
        recreate();
        return message;
    }

    public static Response success(Object message) {
        return new Response(message, null);
    }

    public static Response failed(Throwable cause) {
        return new Response(null, cause);
    }
}
