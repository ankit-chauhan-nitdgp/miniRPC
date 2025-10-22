package projects.ankit.core;

import projects.ankit.util.Serializer;
import java.io.*;
import java.net.Socket;

public class ProtocolHandler {

    public static void sendRequest(Socket socket, Request req) throws IOException {
        byte[] payload = Serializer.serialize(req);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(payload.length);
        dos.write(payload);
        dos.flush();
    }

    public static Request readRequest(Socket socket) throws IOException, ClassNotFoundException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        int length = dis.readInt();
        byte[] data = new byte[length];
        dis.readFully(data);
        return (Request) Serializer.deserialize(data);
    }

    public static void sendResponse(Socket socket, Response res) throws IOException {
        byte[] payload = Serializer.serialize(res);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(payload.length);
        dos.write(payload);
        dos.flush();
    }

    public static Response readResponse(Socket socket) throws IOException, ClassNotFoundException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        int length = dis.readInt();
        byte[] data = new byte[length];
        dis.readFully(data);
        return (Response) Serializer.deserialize(data);
    }

    public static void sendFrame(Socket socket, Frame frame) throws IOException {
        byte[] bytes = Serializer.serialize(frame);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeInt(bytes.length);
        dos.write(bytes);
        dos.flush();
    }

    public static Frame readFrame(Socket socket)
            throws IOException, ClassNotFoundException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        int len = dis.readInt();
        byte[] buf = new byte[len];
        dis.readFully(buf);
        return (Frame) Serializer.deserialize(buf);
    }

}
