package game;

import game.Process.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * This is the login page where the user inputs its username
 * and its password to connect to the server.
 */
public class Log {
    // The private fields
    protected static JFrame frame = new JFrame("J Tank Trouble");
    private static JTextField userName = new JTextField("User Name");
    private static JPasswordField passwordField = new JPasswordField();
    private static JCheckBox remember = new JCheckBox("Remember me");
    private static JCheckBox showPassword = new JCheckBox("Show Password");
    private static JLabel userLabel = new JLabel(new ImageIcon("src/game/IconsInGame/Farshid/name_48px.png"));
    private static JLabel passwordLabel = new JLabel(new ImageIcon("src/game/IconsInGame/Farshid/key_100px.png"));
    private static JButton logIn = new JButton("Log in"), signUp = new JButton("Sign Up");
    private static JLabel logo = new JLabel(new ImageIcon("src/game/IconsInGame/Logo.png"));

    public static void run() {// TODO: 21-Jul-20 local save mishe inke remember beshe
        frame.setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
        frame.setPreferredSize(new Dimension(750, 500));
        frame.setLocation(250, 100);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        init();

        // Creating the login page
        Runnable r = () -> {
            JPanel c = null;
            try {
                c = new MainPanel(ImageIO.read(new File("src/game/IconsInGame/Farshid/background.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert c != null;
            c.setLayout(null);
            logo.setLocation(125, 0);
            userLabel.setLocation(250, 125);
            userName.setLocation(330, 155);
            passwordLabel.setLocation(250, 175);
            passwordField.setLocation(330, 205);
            remember.setLocation(275, 250);
            showPassword.setLocation(275, 300);
            logIn.setLocation(275, 350);
            signUp.setLocation(365, 350);
            c.add(logo);
            c.add(userLabel);
            c.add(userName);
            c.add(passwordLabel);
            c.add(passwordField);
            c.add(remember);
            c.add(showPassword);
            c.add(logIn);
            c.add(signUp);
            frame.add(c);
            frame.pack();
            frame.setVisible(true);
        };
        SwingUtilities.invokeLater(r);
        logIn.doClick();
    }

    /*
        This method will set the styles to
        the components.
     */
    private static void init() {
        logo.setSize(500, 150);
        logIn.setSize(80, 30);
        signUp.setSize(80, 30);
        userLabel.setSize(90, 90);
        userName.setSize(140, 25);
        passwordLabel.setSize(90, 90);
        passwordField.setSize(140, 25);
        remember.setSize(160, 20);//105
        showPassword.setSize(160, 20);//113
        showPassword.setFont(new Font("Dialog", Font.BOLD, 15));
        remember.setFont(new Font("Dialog", Font.BOLD, 15));
        passwordField.setEchoChar('*');
        remember.setContentAreaFilled(false);
        showPassword.setContentAreaFilled(false);
        remember.setForeground(Color.BLACK);
        showPassword.setForeground(Color.BLACK);
        logIn.setBorder(null);
        signUp.setBorder(null);
        userName.setBorder(null);
        passwordField.setBorder(null);
        passwordField.setText("Password");
        logIn.setBackground(Color.BLACK);
        logIn.setForeground(Color.white);
        signUp.setBackground(Color.BLACK);
        signUp.setForeground(Color.white);
        UIManager.put("Button.select", Color.DARK_GRAY);
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected())
                passwordField.setEchoChar((char) 0);
            else
                passwordField.setEchoChar('*');
        });
        userName.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when a component gains the keyboard focus.
             *
             * @param e todo
             */
            @Override
            public void focusGained(FocusEvent e) {
                if (userName.getText().equals("User Name"))
                    userName.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (userName.getText().equals(""))
                    userName.setText("User Name");
            }
        });

        passwordField.addFocusListener(new FocusAdapter() {
            /**
             * Invoked when a component gains the keyboard focus.
             *
             * @param e todo
             */
            @Override
            public void focusGained(FocusEvent e) {
                if (Arrays.equals(passwordField.getPassword(), "Password".toCharArray()))
                    passwordField.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0)
                    passwordField.setText("Password");
            }
        });

        initButtons();
    }

    private static void initButtons() {
        logIn.addActionListener(e -> new SwingWorker<>() {
            /**
             * Computes a result, or throws an exception if unable to do so.
             *
             * <p>
             * Note that this method is executed only once.
             *
             * <p>
             * Note: this method is executed in a background thread.
             *
             * @return the computed result
             * @throws Exception if unable to compute a result
             */
            @Override
            protected Object doInBackground() {
                String ip = "127.0.0.1";//you can change it later .
                int port = 1726;//you can change the port later too.
                String ret = null;
                try (Socket socket = new Socket(ip, port)) {
                    ret = takeString(socket, "Log in");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                return ret;
            }

            @Override
            protected void done() {
                try {
                    String ret = get().toString();
                    if (ret.equalsIgnoreCase("user entered the game.")) {
                        frame.setVisible(false);
                        Setting.run();
                        return;
                    }
                    if (!userName.getText().equalsIgnoreCase("User Name"))
                        JOptionPane.showMessageDialog(null, get().toString());
                } catch (InterruptedException | ExecutionException | NullPointerException ex) {
                    JOptionPane.showMessageDialog(null, "Your attempt to connect to our servers was failed.", "Error", JOptionPane.PLAIN_MESSAGE);
                }
            }
        }.execute());

        signUp.addActionListener(e -> new SwingWorker<>() {
            /**
             * Computes a result, or throws an exception if unable to do so.
             *
             * <p>
             * Note that this method is executed only once.
             *
             * <p>
             * Note: this method is executed in a background thread.
             *
             * @return the computed result
             * @throws Exception if unable to compute a result
             */
            @Override
            protected Object doInBackground() {
                String ip = "127.0.0.1";//you can change it later .
                int port = 1726;//you can change the port later too.
                String ret = null;
                try (Socket socket = new Socket(ip, port)) {
                    ret = takeString(socket, "Sign up");
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
                return ret;
            }

            @Override
            protected void done() {
                try {
                    JOptionPane.showMessageDialog(null, get().toString());
                } catch (InterruptedException | ExecutionException | NullPointerException ex) {
                    JOptionPane.showMessageDialog(null, "Your attempt to connect to our servers was failed.", "Error", JOptionPane.PLAIN_MESSAGE);
                }
            }
        }.execute());
    }

    private static String takeString(Socket socket, String s) throws IOException {
        Scanner in = new Scanner(socket.getInputStream());
        PrintStream out = new PrintStream(socket.getOutputStream());
        String name = userName.getText();
        String pass = String.valueOf(passwordField.getPassword());
        out.println(s);
        out.println(name);
        out.println(pass);
        if (remember.isSelected())
            out.println("remember");
        else
            out.println("don't remember");
        String tr = in.nextLine();
        return tr;
    }

    private static class MainPanel extends JPanel {
        private Image bg;

        MainPanel(Image bg) {
            this.bg = bg;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
