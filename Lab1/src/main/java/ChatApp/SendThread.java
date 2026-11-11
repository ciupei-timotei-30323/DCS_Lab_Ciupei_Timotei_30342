package ChatApp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;

public class SendThread extends Thread {
    protected DatagramSocket socket = null;
    private InetAddress remoteAddress;
    private int remotePort;
    private LinkedBlockingQueue<String> outboundQueue;

    // Modified constructor to accept destination details
    public SendThread(InetAddress remoteAddress, int remotePort) throws IOException {
        super("SendThread");
        this.socket = new DatagramSocket(); // Auto-assign outgoing port
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.outboundQueue = new LinkedBlockingQueue<>();
    }

    // The GUI will call this method when the "Send" button is clicked
    public void queueMessage(String message) {
        outboundQueue.offer(message);
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Blocks here until the GUI drops a message into the queue
                String message = outboundQueue.take();

                byte[] buf = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, remoteAddress, remotePort);
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}