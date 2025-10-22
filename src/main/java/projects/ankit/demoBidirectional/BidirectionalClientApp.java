package projects.ankit.demoBidirectional;

import projects.ankit.client.BidirectionalChatClient;
import projects.ankit.constants.Ports;

import java.net.Socket;

public class BidirectionalClientApp {

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket(Ports.host, Ports.BidirectionalPort)) {

            while (!socket.isClosed() && socket.isBound()) {
                 new BidirectionalChatClient(socket).start();
            }
        }
    }
}
