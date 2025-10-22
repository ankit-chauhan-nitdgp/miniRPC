package projects.ankit.client;

import projects.ankit.core.Frame;
import projects.ankit.core.ProtocolHandler;
import java.net.Socket;
import java.util.Scanner;

public class BidirectionalChatClient {

    private final Socket socket;

    public BidirectionalChatClient(Socket socket) {
        this.socket = socket;
    }

    public void start() {
        System.out.println("Connected to bidirectional RPC server.");
        Thread readThread = new Thread(() -> readLoop(socket));
        readThread.start();
        writeLoop(socket);
    }

    private static void readLoop(Socket socket) {
        try {
            while (true) {
                Frame frame = ProtocolHandler.readFrame(socket);
                if (frame.getType() == Frame.RESPONSE_DATA) {
                    System.out.println("Server: " + frame.getPayload());
                } else if (frame.getType() == Frame.RESPONSE_END) {
                    System.out.println("Server ended chat.");
                    socket.close();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Read loop closed: " + e.getMessage());
        }
    }

    private static void writeLoop(Socket socket) {
        Scanner sc = new Scanner(System.in);
        try {
            while (!socket.isClosed()) {
                System.out.print("Client> ");
                String msg = sc.nextLine();
                if (msg.equalsIgnoreCase("exit")) {
                    Frame end = new Frame(1, Frame.RESPONSE_END, "bye");
                    ProtocolHandler.sendFrame(socket, end);
                    socket.close();
                    break;
                }
                Frame f = new Frame(1, Frame.REQUEST_DATA, msg);
                ProtocolHandler.sendFrame(socket, f);
            }
        } catch (Exception e) {
            System.out.println("Write loop closed: " + e.getMessage());
        }
    }

}
