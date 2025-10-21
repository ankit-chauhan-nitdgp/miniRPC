package projects.ankit.client;

import projects.ankit.core.*;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class RpcClient {
    private final String host;
    private final int port;
    private final AtomicInteger counter = new AtomicInteger(0);

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Object call(String service, String method, Object... params) {
        int id = counter.incrementAndGet();
        Request req = new Request(id, service + "#" + method, params);
        try (Socket socket = new Socket(host, port)) {
            ProtocolHandler.sendRequest(socket, req);
            Response res = ProtocolHandler.readResponse(socket);
            if (res.getStatusCode() != 0) throw new RuntimeException((String) res.getResult());
            return res.getResult();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
