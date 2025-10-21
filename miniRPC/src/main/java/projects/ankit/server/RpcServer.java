package projects.ankit.server;

import projects.ankit.core.*;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcServer {
    private final int port;
    private final MethodRegistry registry;

    public RpcServer(int port, MethodRegistry registry) {
        this.port = port;
        this.registry = registry;
    }

    public void start() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("RPC Server started on port " + port);
            while (!serverSocket.isClosed() && serverSocket.isBound()) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start(); // for few parallel threading, should use NIO production level
            }
        }
    }

    private void handleClient(Socket socket) {
        try {
            Request req = ProtocolHandler.readRequest(socket);
            Object result = registry.invoke(req);
            Response res = new Response(req.getRequestId(), 0, result);
            ProtocolHandler.sendResponse(socket, res);
        } catch (Exception e) {
            try {
                ProtocolHandler.sendResponse(socket, new Response(-1, 1, e.getMessage()));
            } catch (Exception ignored) {}
        }
    }
}
