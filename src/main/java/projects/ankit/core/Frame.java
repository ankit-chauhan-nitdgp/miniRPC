package projects.ankit.core;

import java.io.Serializable;

public class Frame implements Serializable {
    public static final byte REQUEST_START = 0;
    public static final byte RESPONSE_DATA = 1;
    public static final byte RESPONSE_END  = 2;
    public static final byte ERROR = 3;
    public static final byte RESPONSE_START = 4;
    public static final byte REQUEST_DATA = 5;
    public static final byte REQUEST_END  = 6;

    public int streamId;
    public byte type;
    public Object payload;

    public Frame(int streamId, byte type, Object payload) {
        this.streamId = streamId;
        this.type = type;
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }

    public byte getType() {
        return type;
    }

    public int getStreamId() {
        return streamId;
    }
}
