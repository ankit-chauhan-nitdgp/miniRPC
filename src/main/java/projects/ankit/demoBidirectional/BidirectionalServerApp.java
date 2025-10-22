package projects.ankit.demoBidirectional;

import projects.ankit.constants.Ports;
import projects.ankit.server.BidirectionalChatServer;

import java.net.ServerSocket;
import java.net.Socket;

public class BidirectionalServerApp {
    public static void main(String[] args) throws Exception {
        try (ServerSocket server = new ServerSocket(Ports.BidirectionalPort)) {
            System.out.println("Bidirectional RPC server running on port "+ Ports.BidirectionalPort);
            while (!server.isClosed() && server.isBound()) {
                Socket socket = server.accept();
                new Thread(new BidirectionalChatServer(socket)).start();
            }
        }
    }
}
