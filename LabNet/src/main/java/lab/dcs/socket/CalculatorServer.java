package lab.dcs.socket;

import java.io.*;
import java.net.*;

public class CalculatorServer {
    public static void main(String[] args) {
        int port = 1901;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Calculator Server is running on port " + port + "...");

            while (true) {
                // Wait for a client connection
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true)) {

                    InetSocketAddress remoteAddr = (InetSocketAddress) socket.getRemoteSocketAddress();
                    System.out.println("Client connected: " + remoteAddr.getHostName() + ":" + remoteAddr.getPort());

                    String line;
                    // Protocol: expects "num1,num2,op" (e.g. "12.5,4,+")
                    while ((line = in.readLine()) != null) {
                        if (line.equalsIgnoreCase("END")) {
                            break;
                        }

                        System.out.println("Received request: " + line);
                        String[] parts = line.split(",");

                        if (parts.length == 3) {
                            try {
                                double num1 = Double.parseDouble(parts[0].trim());
                                double num2 = Double.parseDouble(parts[1].trim());
                                String op = parts[2].trim();
                                String result;

                                switch (op) {
                                    case "+":
                                        result = String.valueOf(num1 + num2);
                                        break;
                                    case "-":
                                        result = String.valueOf(num1 - num2);
                                        break;
                                    case "*":
                                        result = String.valueOf(num1 * num2);
                                        break;
                                    case "/":
                                        if (num2 == 0) {
                                            result = "Error: Division by zero";
                                        } else {
                                            result = String.valueOf(num1 / num2);
                                        }
                                        break;
                                    default:
                                        result = "Error: Invalid operator";
                                        break;
                                }
                                out.println(result);
                            } catch (NumberFormatException e) {
                                out.println("Error: Invalid numbers format");
                            }
                        } else {
                            out.println("Error: Malformed request");
                        }
                    }
                    System.out.println("Client disconnected.");
                } catch (IOException e) {
                    System.err.println("Communication error with client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}