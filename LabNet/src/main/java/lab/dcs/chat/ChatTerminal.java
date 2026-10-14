package lab.dcs.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ChatTerminal extends JFrame {

    private JTextArea displayArea;
    private JTextField inputField;
    private JButton sendButton;

    private DatagramSocket socket;
    private InetAddress peerAddress;
    private int peerPort;

    private String userName;

    // blocking queue used by the sender thread to wait for outgoing messages
    private final BlockingQueue<String> outgoingMessages = new LinkedBlockingQueue<>();

    public ChatTerminal(String userName, int localPort, int peerPort) throws SocketException, UnknownHostException {
        super("Chat - " + userName + " (port " + localPort + ")");
        this.userName = userName;
        this.peerPort = peerPort;
        this.peerAddress = InetAddress.getByName("localhost");
        this.socket = new DatagramSocket(localPort);

        initGUI();

        // create the two required threads: one for sending, one for receiving
        Thread senderThread = new Thread(new SenderThread(), "SenderThread");
        Thread receiverThread = new Thread(new ReceiverThread(), "ReceiverThread");
        senderThread.setDaemon(true);
        receiverThread.setDaemon(true);
        senderThread.start();
        receiverThread.start();
    }

    private void initGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        // text field for displaying received and sent messages in order
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        // bottom panel: text field for writing a message + send button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // send action on button click
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                queueMessage();
            }
        });

        // send action on Enter key press
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                queueMessage();
            }
        });

        setVisible(true);
    }

    /**
     * Takes the text from the input field and places it into the outgoing queue
     * for the sender thread to pick up.
     */
    private void queueMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        outgoingMessages.offer(text);
        inputField.setText("");
    }

    /**
     * Persistent sender thread.
     * Waits on a blocking queue for messages and sends each one as a UDP datagram.
     */
    private class SenderThread implements Runnable {
        public void run() {
            while (true) {
                try {
                    // blocks until a message is available
                    String message = outgoingMessages.take();

                    String fullMessage = userName + ": " + message;
                    byte[] buf = fullMessage.getBytes();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, peerAddress, peerPort);
                    socket.send(packet);

                    // display sent message in the text area (on the EDT)
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            displayArea.append("[Me] " + message + "\n");
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Persistent receiver thread.
     * Continuously listens for incoming UDP datagrams and displays them.
     */
    private class ReceiverThread implements Runnable {
        public void run() {
            byte[] buf = new byte[1024];
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet); // blocking call

                    String received = new String(packet.getData(), 0, packet.getLength());

                    // display received message in the text area (on the EDT)
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            displayArea.append("[Received] " + received + "\n");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
