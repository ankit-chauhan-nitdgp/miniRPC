package projects.ankit.server;

import projects.ankit.core.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientStreamingServer {

    private final int port;
    private final MethodRegistry registry;

    public ClientStreamingServer(int port, MethodRegistry registry) {
        this.port = port;
        this.registry = registry;
    }

    public void start() throws Exception {
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Client-streaming RPC listening on port " + port);
            while (server.isBound() && !server.isClosed()) {
                Socket socket = server.accept();
                new Thread(() -> handle(socket)).start();
            }
        }
    }


    private void handle(Socket socket) {
        try (socket) {
            Frame first = ProtocolHandler.readFrame(socket);
            if (first.getType() != Frame.REQUEST_START) return;
            Request req = (Request) first.getPayload();

            List<Object> received = new ArrayList<>();

            // collect stream data until end
            while (true) {
                Frame frame = ProtocolHandler.readFrame(socket);
                if (frame.getType() == Frame.REQUEST_DATA) {
                    received.add(frame.getPayload());
                } else if (frame.getType() == Frame.REQUEST_END) {
                    break;
                }
            }

            // invoke actual method with all collected data
            Object result = registry.invoke(
                    new Request(req.getRequestId(), req.getMethodName(), new Object[]{received})
            );

            Response res = new Response(req.getRequestId(), 0, result);
            Frame endFrame = new Frame(req.getRequestId(), Frame.RESPONSE_END, res);
            ProtocolHandler.sendFrame(socket, endFrame);

            System.out.println("Client stream complete for " + req.getMethodName());

        } catch (Exception e) {
            try {
                Frame err = new Frame(0, Frame.ERROR, e.getMessage());
                ProtocolHandler.sendFrame(socket, err);
            } catch (Exception ignored) {}
        }
    }

}
