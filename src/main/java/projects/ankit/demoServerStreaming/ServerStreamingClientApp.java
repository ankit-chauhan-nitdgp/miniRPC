package projects.ankit.demoServerStreaming;

import projects.ankit.client.ServerStreamingClient;
import projects.ankit.constants.Ports;

import java.util.Scanner;

public class ServerStreamingClientApp {

    public static void main(String[] args) {

        //takes input through cli
        Scanner scanner = new Scanner(System.in);

        // initialize client
        ServerStreamingClient client = new ServerStreamingClient(Ports.host, Ports.ServerStreamingPort);

        while (true) {
            System.out.print("Enter a numbers to get prime factors (or 'exit' to quit): ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) break;

            try {
                // Open a new connection for each request
                int payload = Integer.parseInt(line);
                try {
                    // calling a method and get data stream in return using callback
                    client.call("PrimeFactorService", "primeFactors", new ServerStreamingClient.ResultStream() {
                        @Override public void onNext(Object data) {
                            System.out.println("Factors: " + data);
                        }
                        @Override public void onComplete(Object result) {
                            System.out.println("Final result: " + result);
                        }
                        @Override public void onError(String message) {
                            System.err.println("Error: " + message);
                        }
                    }, payload);

                }catch (RuntimeException e){
                    System.out.println("Cannot connect to server: Connection Refused");
                }


            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter integers only.");
            }
        }

        System.out.println("Stream Client closed.");
        scanner.close();

    }
}
