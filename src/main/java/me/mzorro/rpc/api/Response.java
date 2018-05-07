package me.mzorro.rpc.api;

/**
 * Created On 05/02 2018
 *
 * @author mzorrox@gmail.com
 */
public class Response {

    private Object response;

    private Throwable throwable;

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Object recreate() throws Throwable {
        if (throwable != null) {
            throw throwable;
        } else {
            return response;
        }
    }
}
