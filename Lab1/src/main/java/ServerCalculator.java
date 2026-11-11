import java.io.*;
import java.net.*;

public class ServerCalculator {
    public static final int PORT = 1900;

    void startServer() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(PORT);
            System.out.println("Server is waiting for connections...");
            while (true) {
                Socket socket = ss.accept();

                // Log connection details like in SimpleServer
                InetSocketAddress remoteadr = (InetSocketAddress) socket.getRemoteSocketAddress();
                String remotehost = remoteadr.getHostName();
                int remoteport = remoteadr.getPort();
                System.out.println("New client connected: " + remotehost + ":" + remoteport);

                new TreatClient(socket).start();
            }
        } catch (IOException ex) {
            System.err.println("Error: " + ex.getMessage());
        } finally {
            try {
                if (ss != null) ss.close();
            } catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ServerCalculator server = new ServerCalculator();
        server.startServer();
    }
}

class TreatClient extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    TreatClient(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    public void run() {
        String line = "";
        try {
            while (true) {
                line = in.readLine(); // Read data from the client (e.g., "10 + 5")
                if (line == null || line.equals("END")) {
                    break;
                }
                System.out.println("Server received: " + line);

                String result = calculate(line);
                out.println(result); // Send calculated result back to the client
                out.flush();
            }
            System.out.println("closing...");
        } catch (IOException e) {
            System.err.println("IO Exception");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }

    private String calculate(String expression) {
        try {
            // Expected format from ClientGUI: "number1 operator number2" (separated by spaces)
            String[] parts = expression.trim().split("\\s+");
            double num1 = Double.parseDouble(parts[0]);
            String op = parts[1];
            double num2 = Double.parseDouble(parts[2]);

            switch (op) {
                case "+":
                    return String.valueOf(num1 + num2);
                case "-":
                    return String.valueOf(num1 - num2);
                case "*":
                    return String.valueOf(num1 * num2);
                case "/":
                    if (num2 == 0) return "Error: Div by 0";
                    return String.valueOf(num1 / num2);
                default:
                    return "Error: Unknown op";
            }
        } catch (Exception e) {
            return "Error: Invalid input";
        }
    }
}