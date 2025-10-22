package projects.ankit.client;

import projects.ankit.core.Frame;
import projects.ankit.core.ProtocolHandler;
import projects.ankit.core.Request;
import projects.ankit.core.Response;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientStreamingClient {


    private final String host;
    private final int port;
    private final AtomicInteger counter = new AtomicInteger(0);

    public ClientStreamingClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public interface ClientStreamingResults {
        void onNext(Object data);
        void onComplete(Object result);
        void onError(String message);
    }

    public void call(String service, String method, List<Object> chunks, ClientStreamingResults listener) {
        int id = counter.incrementAndGet();
        // create Request + Frame
        Request req = new Request(id, service + "#" + method, new Object[]{});
        Frame start = new Frame(id, Frame.REQUEST_START, req);

        try (Socket socket = new Socket(host, port)) {

            // send start
            ProtocolHandler.sendFrame(socket, start);

            // send chunks
            for (Object chunk : chunks) {
                Frame data = new Frame(id, Frame.REQUEST_DATA, chunk);
                ProtocolHandler.sendFrame(socket, data);
            }

            // mark end of stream
            Frame end = new Frame(id, Frame.REQUEST_END, "done");
            ProtocolHandler.sendFrame(socket, end);

            // wait for single response
            Frame responseFrame = ProtocolHandler.readFrame(socket);
            if (responseFrame.getType() == Frame.RESPONSE_END) {
                Response res = (Response) responseFrame.getPayload();
                listener.onComplete(res.getResult());
            } else if (responseFrame.getType() == Frame.ERROR) {
                listener.onError(String.valueOf(responseFrame.getPayload()));
            }

        } catch (Exception e) {
            listener.onError(e.getMessage());
        }
    }

}
