package projects.ankit.server;

import projects.ankit.core.Frame;
import projects.ankit.core.ProtocolHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class BidirectionalChatServer implements Runnable {
    private final Socket socket;

    public BidirectionalChatServer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            System.out.println("Client connected for chat: " + socket.getRemoteSocketAddress());
            Thread readThread = new Thread(() -> readLoop(socket));
            readThread.start();
            writeLoop(socket);
        } catch (Exception e) {
            System.err.println("Chat server error: " + e.getMessage());
        }
    }

    private void readLoop(Socket socket) {
        try {
            while (true) {
                Frame frame = ProtocolHandler.readFrame(socket);
                if (frame.getType() == Frame.REQUEST_DATA) {
                    System.out.println("Client: " + frame.getPayload());
                    // echo back a reply
                    Frame reply = new Frame(frame.getStreamId(), Frame.RESPONSE_DATA,
                            "Server received: " + frame.getPayload());
                    ProtocolHandler.sendFrame(socket, reply);
                } else if (frame.getType() == Frame.RESPONSE_END) {
                    System.out.println("Client ended stream.");
                    socket.close();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Read loop ended: " + e.getMessage());
        }
    }

    private void writeLoop(Socket socket) {
        Scanner sc = new Scanner(System.in);
        try {
            while (!socket.isClosed()) {
                System.out.print("Server> ");
                String msg = sc.nextLine();
                if (msg.equalsIgnoreCase("exit")) {
                    Frame end = new Frame(1, Frame.RESPONSE_END, "bye");
                    ProtocolHandler.sendFrame(socket, end);
                    socket.close();
                    break;
                }
                Frame f = new Frame(1, Frame.RESPONSE_DATA, msg);
                ProtocolHandler.sendFrame(socket, f);
            }
        } catch (IOException e) {
            System.out.println("Write loop ended: " + e.getMessage());
        }
    }
}
