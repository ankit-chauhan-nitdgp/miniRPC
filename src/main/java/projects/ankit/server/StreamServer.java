package projects.ankit.server;

import projects.ankit.core.*;
import projects.ankit.util.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class StreamServer {
    private final int port;
    private final MethodRegistry registry;

    public StreamServer(int port, MethodRegistry registry) {
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
        try (socket;
             var in = new DataInputStream(socket.getInputStream());
             var out = new DataOutputStream(socket.getOutputStream())) {

            int len = in.readInt();
            byte[] data = in.readNBytes(len);
            Frame req = (Frame) Serializer.deserialize(data);


            Request request = new Request(req.streamId, req.methodName, req.payload);
            List<Integer> result = (List<Integer>) registry.invoke(request);


//            System.out.println("Received request streamId=" + req.streamId + " payload=" + req.payload);

//            int n = (Integer) req.payload;
//
//            List<Integer> factors = PrimeUtil.primeFactors(n);

            for (int f : result) {
                Frame resp = new Frame(req.streamId, Frame.RESPONSE_DATA, req.methodName, new Object[]{f});
                byte[] encoded = Serializer.serialize(resp);
                out.writeInt(encoded.length);
                out.write(encoded);
                out.flush();
                Thread.sleep(200); // simulating streaming delay
            }


            Frame end = new Frame(req.streamId, Frame.RESPONSE_END, req.methodName,new Object[]{"done"});
            byte[] endBytes = Serializer.serialize(end);
            out.writeInt(endBytes.length);
            out.write(endBytes);
            out.flush();

            System.out.println("Stream completed for " + Arrays.toString(req.payload));

        } catch (Exception e) {
            System.out.println("Stream server unable to handle request"+e.getMessage());
        }
    }
}
