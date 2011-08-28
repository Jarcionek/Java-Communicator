package javacommunicator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    private static ObjectOutputStream out = null;
    private static ObjectInputStream in = null;
    private static JTextArea textArea = null;
    private static boolean connected = false;

    public static void main(String[] args) throws Exception {
        // get user name, IP and port
        String title = "Java communicator";
        final String name = JOptionPane.showInputDialog(null, "Give your name:",
                title, JOptionPane.PLAIN_MESSAGE);
        int port = Integer.parseInt(JOptionPane.showInputDialog(null, "Give port:",
                title, JOptionPane.PLAIN_MESSAGE));
        String IP = JOptionPane.showInputDialog(null, "Give IP or leave blank "
                + "to set server", title, JOptionPane.PLAIN_MESSAGE);

        // create gui
        JFrame frame = new JFrame(title);
        JPanel contentPane = new JPanel(new BorderLayout());
        textArea = new JTextArea();
        textArea.setEditable(false);
        final JTextField textField = new JTextField();
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (connected) {
                    sendMsg(name + ": " + textField.getText());
                    log("me: " + textField.getText());
                    textField.setText("");
                }
            }
        });
        contentPane.add(new JScrollPane(textArea), BorderLayout.CENTER);
        contentPane.add(textField, BorderLayout.SOUTH);
        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sendMsg(name + " has closed communicator");
                System.exit(0);
            }
        });
        frame.setSize(300, 200);
        frame.setVisible(true);

        // create socket
        Socket s;
        if (IP.equals("")) {
            ServerSocket ss = new ServerSocket(port);
            log("Server set at port: " + port + "\n");
            s = ss.accept();
            log(s.getInetAddress() + " connected to you" + "\n");
            sendMsg(name + ": you are connected");
        } else {
            s = new Socket(IP, port);
            log("connected");
        }
        connected = true;

        // create streams
        out = new ObjectOutputStream(s.getOutputStream());
        out.flush();
        in = new ObjectInputStream(s.getInputStream());

        // wait for messages
        while (true) {
            try {
                log(in.readObject().toString());
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(0);
            }
        }

    }

    private static void sendMsg(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static synchronized void log(String msg) {
        textArea.append(msg + "\n");
    }
}