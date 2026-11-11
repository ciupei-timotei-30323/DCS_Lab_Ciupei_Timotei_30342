package ChatApp;

import javax.swing.*;
import java.awt.*;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ChatTerminal extends JFrame {
    private JTextArea chatHistory;
    private JTextField chatInput;
    private JButton sendButton;

    private DatagramSocket receiveSocket;
    private SendThread sendThread;
    private ReceiveThread receiveThread;

    public ChatTerminal(int localPort, String remoteIp, int remotePort) throws Exception {
        this.receiveSocket = new DatagramSocket(localPort);
        InetAddress remoteAddress = InetAddress.getByName(remoteIp);

        this.sendThread = new SendThread(remoteAddress, remotePort);
        this.receiveThread = new ReceiveThread(receiveSocket, this);

        // 2. Start the threads
        this.sendThread.start();
        this.receiveThread.start();

        setupGUI();
    }

    private void setupGUI() {
        setTitle("Chat Terminal");
        setLayout(new BorderLayout());

        chatHistory = new JTextArea();
        chatHistory.setEditable(false);
        add(new JScrollPane(chatHistory), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        chatInput = new JTextField();
        sendButton = new JButton("Send");

        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());

        setSize(400,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void sendMessage() {
        String text = chatInput.getText();
        if (!text.isEmpty()) {
            appendMessage(">> " + text);

            sendThread.queueMessage(text);
            chatInput.setText("");
        }
    }

    public void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> chatHistory.append(message+ "\n"));
    }

    public static void main(String[] args) {
        try {
            // Launch Terminal A (Listens on 1977, Sends to 1978)
            ChatTerminal terminalA = new ChatTerminal(1977, "localhost", 1978);
            terminalA.setTitle("Terminal A (Port 1977)");
            terminalA.setLocation(100, 100);

            // Launch Terminal B (Listens on 1978, Sends to 1977)
            ChatTerminal terminalB = new ChatTerminal(1978, "localhost", 1977);
            terminalB.setTitle("Terminal B (Port 1978)");
            terminalB.setLocation(550, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
