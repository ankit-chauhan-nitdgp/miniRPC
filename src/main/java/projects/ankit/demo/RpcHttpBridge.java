package projects.ankit.demo;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import projects.ankit.client.RpcClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class RpcHttpBridge {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/add", RpcHttpBridge::handleAdd);
        server.start();
        System.out.println("HTTP bridge on http://localhost:8080");
    }

    private static void handleAdd(HttpExchange exchange) throws IOException {
        try {
            // simple ?a=10&b=20
            var params = exchange.getRequestURI().getQuery().split("&");
            int a = Integer.parseInt(params[0].split("=")[1]);
            int b = Integer.parseInt(params[1].split("=")[1]);

            RpcClient client = new RpcClient("localhost", 9000);
            client.call("CalculatorService", "add", new RpcClient.ResultListener() {
                @Override
                public void result(Object res) throws IOException {
                    byte[] response = ("Result: " + res).getBytes();
                    exchange.sendResponseHeaders(200, response.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response);
                    }
                }
            }, a, b);

        } catch (Exception e) {
            byte[] err = ("Error: " + e.getMessage()).getBytes();
            exchange.sendResponseHeaders(500, err.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(err);
            }
        }
    }
}
