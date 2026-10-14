package lab.dcs.chat;
import lab.dcs.chat.*;
import java.net.*;

/**
 * Launches Terminal 2 of the UDP chat application.
 * This terminal listens on port 5002 and sends messages to port 5001.
 */
public class Terminal2 {

    public static void main(String[] args) {
        try {
            ChatTerminal terminal = new ChatTerminal("User2", 5002, 5001);
            terminal.setLocation(650, 200);
        } catch (SocketException e) {
            System.err.println("Could not bind to port 5002: " + e.getMessage());
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
        }
    }
}
