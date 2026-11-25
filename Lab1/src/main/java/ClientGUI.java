import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientGUI {
    private JPanel panel1;
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JTextField textField3;

    private BufferedReader in;
    private PrintWriter out;

    public ClientGUI(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;

        // Every button automatically sends whatever symbol (+, -, *, /) is written on it!
        ActionListener operatorListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JButton clickedButton = (JButton) actionEvent.getSource();
                String operator = clickedButton.getText().trim();
                sendOperation(operator);
            }
        };

        button1.addActionListener(operatorListener);
        button2.addActionListener(operatorListener);
        button3.addActionListener(operatorListener);
        button4.addActionListener(operatorListener);
    }

    private void sendOperation(String operator) {
        try {
            String num1 = textField1.getText().trim(); // "No. 1:" box
            String num2 = textField3.getText().trim(); // "No. 2:" box

            // Send formatted expression to the server (e.g., "1 + 5")
            out.println(num1 + " " + operator + " " + num2);

            // Wait for the server's response and display it in textField2 ("Result:" box)
            String response = in.readLine();
            textField2.setText(response);
        } catch (IOException e) {
            textField2.setText("Communication Error");
            e.printStackTrace();
        }
    }

    public JPanel getPanel1() {
        return panel1;
    }
}