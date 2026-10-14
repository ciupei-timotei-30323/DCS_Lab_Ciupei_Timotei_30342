package lab.dcs.chat;

import java.net.*;

/**
 * Launches Terminal 1 of the UDP chat application.
 * This terminal listens on port 5001 and sends messages to port 5002.
 */
public class Terminal1 {

    public static void main(String[] args) {
        try {
            ChatTerminal terminal = new ChatTerminal("User1", 5001, 5002);
            terminal.setLocation(100, 200);
        } catch (SocketException e) {
            System.err.println("Could not bind to port 5001: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
        }
    }
}
