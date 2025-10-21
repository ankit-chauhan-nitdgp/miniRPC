package projects.ankit.demo;

import java.io.IOException;
import java.util.Scanner;
import projects.ankit.client.RpcClient;

public class ClientApp {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        RpcClient client = new RpcClient("localhost", 9000);

        while (true) {
            System.out.print("Enter two numbers (or 'exit' to quit): ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) break;

            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                System.out.println("Please enter exactly two numbers.");
                continue;
            }

            try {
                int a = Integer.parseInt(parts[0]);
                int b = Integer.parseInt(parts[1]);
                try {
                    Object result = client.call("CalculatorService", "add", a, b);
                    System.out.println("Result: " + result);
                }catch (RuntimeException e){
                    System.out.println("Cannot connect to server: Connection Refused");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter integers only.");
            }
        }

        System.out.println("Client closed.");
        scanner.close();
    }
}
