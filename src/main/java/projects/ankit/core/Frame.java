package projects.ankit.core;

import java.io.Serializable;

public class Frame implements Serializable {
    public static final byte REQUEST_START = 0;
    public static final byte RESPONSE_DATA = 1;
    public static final byte RESPONSE_END  = 2;

    public int streamId;
    public byte type;
    public String methodName;
    public Object[] payload;

    public Frame(int streamId, byte type, String methodName, Object[] payload) {
        this.streamId = streamId;
        this.type = type;
        this.methodName = methodName;
        this.payload = payload;
    }
}
