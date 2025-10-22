package projects.ankit.client;

import projects.ankit.core.Frame;
import projects.ankit.core.ProtocolHandler;
import projects.ankit.core.Request;
import projects.ankit.core.Response;
import projects.ankit.util.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamClient {


    private final String host;
    private final int port;
    private final AtomicInteger counter = new AtomicInteger(0);

    public StreamClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public interface ResultStream {
        void result(Byte type, Object res);
    }

    public void call(String service, String method, ResultStream listener, Object... params) {

        int id = counter.incrementAndGet();
        Frame start = new Frame(id, Frame.REQUEST_START, service + "#" + method, params);

        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            System.out.println("Connected to stream server");

            // send request frame

            byte[] startBytes = Serializer.serialize(start);
            out.writeInt(startBytes.length);
            out.write(startBytes);
            out.flush();

            // read streaming responses
            while (true) {
                int len = in.readInt();
                byte[] data = in.readNBytes(len);
                Frame frame = (Frame) Serializer.deserialize(data);

                if (frame.type == Frame.RESPONSE_DATA) {
                    listener.result(Frame.RESPONSE_DATA, Arrays.stream(frame.payload).findFirst().get());
                } else if (frame.type == Frame.RESPONSE_END) {
                    listener.result(Frame.RESPONSE_END, Arrays.stream(frame.payload).findFirst().get());
                    break;
                }
            }
        } catch (RuntimeException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
