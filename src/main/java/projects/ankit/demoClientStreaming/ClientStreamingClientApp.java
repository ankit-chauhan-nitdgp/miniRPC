package projects.ankit.demoClientStreaming;

import java.util.*;
import java.util.List;

import projects.ankit.client.ClientStreamingClient;
import projects.ankit.constants.Ports;

public class ClientStreamingClientApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ClientStreamingClient client = new ClientStreamingClient(Ports.host, Ports.ClientStreamingPort);

        System.out.println("Enter numbers to calculate average.");
        System.out.println("Type 'done' when finished, or 'exit' to quit.\n");

        while (true) {
            List<Object> itemList = new ArrayList<>();

            while (true) {
                System.out.print("Enter number (or 'done' to calculate / 'exit' to quit): ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Stream Client closed.");
                    scanner.close();
                    return;
                }

                if (input.equalsIgnoreCase("done")) {
                    break; // proceed to send collected items
                }

                try {
                    itemList.add(Integer.parseInt(input));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Enter integers only.");
                }
            }

            if (itemList.isEmpty()) {
                System.out.println("No items entered.");
                continue;
            }

            try {
                client.call(
                        "AverageService",
                        "computeAverage",
                        itemList,
                        new ClientStreamingClient.ClientStreamingResults() {
                            @Override
                            public void onNext(Object data) {
                                // no intermediate chunks expected
                            }

                            @Override
                            public void onComplete(Object result) {
                                System.out.println("Average: " + result);
                            }

                            @Override
                            public void onError(String msg) {
                                System.err.println("Error: " + msg);
                            }
                        });
            } catch (Exception e) {
                System.err.println("Cannot connect to server: " + e.getMessage());
            }
        }
    }
}
