package projects.ankit.server;

import projects.ankit.core.*;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerStreamingServer {
    private final int port;
    private final MethodRegistry registry;

    public ServerStreamingServer(int port, MethodRegistry registry) {
        this.port = port;
        this.registry = registry;
    }

    public void start() throws Exception {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server-streaming RPC listening on port " + port);
            while (server.isBound() && !server.isClosed()) {
                Socket socket = server.accept();
                new Thread(() -> handle(socket)).start();
            }
        }
    }

    private void handle(Socket socket) {
        try (socket) {
            // Read first frame (request)
            Frame frame = ProtocolHandler.readFrame(socket);
            if (frame.getType() != Frame.REQUEST_START) return;

            Request req = (Request) frame.getPayload();
            System.out.println("Received streaming request: " + req.getMethodName());

            // Call user method (must return Iterable, List, or array ) for streaming
            Object invokedResult = registry.invoke(req);
            Iterable<?> results = toIterable(invokedResult);

            // Send each element as RESPONSE_DATA
            for (Object item : results) {
                Frame dataFrame = new Frame(frame.getStreamId(), Frame.RESPONSE_DATA, item);
                ProtocolHandler.sendFrame(socket, dataFrame);
                Thread.sleep(150); // simulate delay
            }

            // Send final RESPONSE_END with Response wrapper
            Response endRes = new Response(req.getRequestId(), 0, "done");
            Frame endFrame = new Frame(frame.getStreamId(), Frame.RESPONSE_END, endRes);
            ProtocolHandler.sendFrame(socket, endFrame);

            System.out.println("Stream completed for " + req.getMethodName());

        } catch (Exception e) {
            try {
                Frame err = new Frame(0, Frame.ERROR, e.getMessage());
                ProtocolHandler.sendFrame(socket, err);
            } catch (Exception ignored) {}
            System.out.println("Stream server error: " + e.getMessage());
        }
    }

    private Iterable<?> toIterable(Object obj) {
        if (obj instanceof Iterable<?> it) return it;
        if (obj != null && obj.getClass().isArray()) {
            return java.util.Arrays.asList((Object[]) obj);
        }
        return java.util.List.of(); // empty
    }
}
