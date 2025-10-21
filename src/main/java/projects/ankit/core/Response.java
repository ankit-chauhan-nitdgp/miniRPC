package projects.ankit.core;

import java.io.Serializable;

public class Response implements Serializable {
    private int requestId;
    private int statusCode; // 0 = OK, 1 = ERROR
    private Object result;

    public Response(int requestId, int statusCode, Object result) {
        this.requestId = requestId;
        this.statusCode = statusCode;
        this.result = result;
    }

    public int getRequestId() { return requestId; }
    public int getStatusCode() { return statusCode; }
    public Object getResult() { return result; }
}
