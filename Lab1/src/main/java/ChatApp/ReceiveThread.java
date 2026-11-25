package ChatApp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import javax.swing.SwingUtilities;

public class ReceiveThread extends Thread {
    private DatagramSocket socket;
    private ChatTerminal gui; // Reference to your GUI

    // Constructor to pass the socket and GUI reference
    public ReceiveThread(DatagramSocket socket, ChatTerminal gui) {
        this.socket = socket;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet); // Blocks until message arrives

                String receivedText = new String(packet.getData(), 0, packet.getLength());

                // Update the GUI safely from this background thread
                gui.appendMessage("> " + receivedText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}