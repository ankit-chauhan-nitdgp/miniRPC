package projects.ankit.demo;

import projects.ankit.client.StreamClient;
import projects.ankit.core.Frame;

import java.util.Scanner;

public class StreamClientApp {

    public static void main(String[] args) {

        //takes input through cli
        Scanner scanner = new Scanner(System.in);

        // initialise client
        StreamClient client = new StreamClient("localhost", 9001);

        while (true) {
            System.out.print("Enter a numbers to get prime factors (or 'exit' to quit): ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) break;


            try {

                // Open a new connection for each request
                int payload = Integer.parseInt(line);

                try {

                    // calling a method and get data stream in return using callback
                    client.call("PrimeFactorService", "primeFactors",
                            (type, res) -> { // reading value using callback
                                if (type == Frame.RESPONSE_DATA){
                                    System.out.println("Factor : " +res);
                                }else if(type == Frame.RESPONSE_END){
                                    System.out.println("Stream End : "+res);
                                }

                            }
                            , payload);

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
