import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            // Creating object address that identifies the server address
            InetAddress address = InetAddress.getByName("127.0.0.1");
            // Connect to port 1900 to match ServerCalculator
            Socket socket = new Socket(address, 1900);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Output is automatically flushed by PrintWriter:
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            // Declare and open the GUI, passing the streams to it
            ClientGUI gui = new ClientGUI(in, out);

            JFrame frame = new JFrame("Client Calculator");
            frame.setContentPane(gui.getPanel1());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);

            // When the user closes the GUI window, send "END" and close the socket
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        out.println("END"); // Causes the server to close the connection
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            frame.setVisible(true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}