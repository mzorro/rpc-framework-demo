package me.mzorro.rpc.api;

import java.io.Serializable;

/**
 * Created On 05/07 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class Invocation implements Serializable {

    private static final long serialVersionUID = -8791282405714720371L;

    private String methodName;

    private Class<?>[] argTypes;

    private Object[] args;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class<?>[] argTypes) {
        this.argTypes = argTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
