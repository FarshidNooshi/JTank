package game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Log {
    private static JTextField userName = new JTextField("User Name");
    private static JPasswordField passwordField = new JPasswordField();
    private static JCheckBox remember = new JCheckBox("Remember me");
    private static JCheckBox showPassword = new JCheckBox("Show Password");
    private static JLabel userLabel = new JLabel(new ImageIcon("src/game/IconsInGame/Farshid/name_48px.png"));
    private static JLabel passwordLabel = new JLabel(new ImageIcon("src/game/IconsInGame/Farshid/key_100px.png"));
    private static JButton logIn = new JButton("Log in"), signUp = new JButton("Sign Up");

    public static void run() {// TODO: 21-Jul-20 local save mishe inke remember beshe
        JFrame frame = new JFrame("J Tank Trouble");
        frame.setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setLocation(450, 150);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        init();
        Runnable r = () -> {
            JPanel c = null;
            try {
                c = new MainPanel(ImageIO.read(new File("src/game/IconsInGame/Farshid/background.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert c != null;
            c.setLayout(null);
            userLabel.setLocation(150, 50);
            userName.setLocation(200, 55);
            passwordLabel.setLocation(150, 100);
            passwordField.setLocation(200, 105);
            remember.setLocation(150, 150);
            showPassword.setLocation(150, 200);
            logIn.setLocation(150, 250);
            signUp.setLocation(240, 250);
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
    }

    private static void init() {
        logIn.setSize(80, 30);
        signUp.setSize(80, 30);
        userLabel.setSize(40, 40);
        userName.setSize(120, 30);
        passwordLabel.setSize(40, 40);
        passwordField.setSize(120, 30);
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
