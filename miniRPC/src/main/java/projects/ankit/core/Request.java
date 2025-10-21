package projects.ankit.core;

import java.io.Serializable;

public class Request implements Serializable {
    private int requestId;
    private String methodName;
    private Object[] params;

    public Request(int requestId, String methodName, Object[] params) {
        this.requestId = requestId;
        this.methodName = methodName;
        this.params = params;
    }

    public int getRequestId() { return requestId; }
    public String getMethodName() { return methodName; }
    public Object[] getParams() { return params; }
}
