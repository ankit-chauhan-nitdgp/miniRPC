package projects.ankit.client;

import projects.ankit.core.Frame;
import projects.ankit.core.ProtocolHandler;
import projects.ankit.core.Request;
import projects.ankit.core.Response;

import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerStreamingClient {

    private final String host;
    private final int port;
    private final AtomicInteger counter = new AtomicInteger(0);

    public ServerStreamingClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public interface ResultStream {
        void onNext(Object data);
        void onComplete(Object result);
        void onError(String message);
    }

    public void call(String service, String method, ResultStream listener, Object... params) {
        int id = counter.incrementAndGet();

        try (Socket socket = new Socket(host, port)) {
            // create Request + Frame
            Request req = new Request(id, service + "#" + method, params);
            Frame start = new Frame(id, Frame.REQUEST_START, req);

            // send Request frame using unified ProtocolHandler
            ProtocolHandler.sendFrame(socket, start);

            // continuously read Frames until stream end
            while (true) {
                Frame frame = ProtocolHandler.readFrame(socket);

                switch (frame.getType()) {
                    case Frame.RESPONSE_DATA -> listener.onNext(frame.getPayload());
                    case Frame.RESPONSE_END -> {
                        Response res = (Response) frame.getPayload();
                        listener.onComplete(res.getResult());
                        return;
                    }
                    case Frame.ERROR -> {
                        listener.onError(String.valueOf(frame.getPayload()));
                        return;
                    }
                    default -> throw new IllegalStateException("Unexpected frame type: " + frame.getType());
                }
            }

        } catch (Exception e) {
            listener.onError(e.getMessage());
        }
    }
}

