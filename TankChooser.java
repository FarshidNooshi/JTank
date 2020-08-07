package game;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Objects;

public class TankChooser extends JFrame {

    private File finalImage = new File("src/game/IconsInGame/Farshid/Tank/tank_blue.png");
    private File finalBullet = new File("src/game/IconsInGame/Farshid/Bullet/bulletBlue1.png");
    private JLabel label = new JLabel();
    private JLabel hours = new JLabel();
    private JLabel results = new JLabel();
    private String username;
    private JButton send;

    public TankChooser(String username) {
        send = new JButton();
        this.username = username;
    }

    public void run() {
        initButtons();

        int response = JOptionPane.showConfirmDialog(this, "Do you want to customize your tank ?");
        if (response == JOptionPane.YES_OPTION) {
            JFrame ask = new JFrame("Choose tank model");
            JPanel c = new MainPanel(new ImageIcon("src/game/IconsInGame/Farshid/background.png").getImage());
            JLabel logo = new JLabel(new ImageIcon("src/game/IconsInGame/Farshid/Logo.png"));
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            c.add(logo);
            logo.setLocation(300, 0);
            logo.setSize(700, 150);
            ask.setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
            // User name information
            creatingUserInfo(c, label, "User name : " + username, 300);
            creatingUserInfo(c, hours, "Hours playing : " + 0, 350);
            creatingUserInfo(c, results, "Total Wins : " + 0 + " Loses : " + 0, 400);
            // Getting the tanks images
            c.setLayout(null);
            ask.setExtendedState(Frame.MAXIMIZED_BOTH);
            ask.setResizable(false);
            // Creating buttons
            CreatingButtons(ask, c);
            ask.add(c);
            ask.setAlwaysOnTop(true);
            ask.setLocationRelativeTo(null);
            ask.setVisible(true);
        } else {
            send.doClick();
        }
    }

    private void CreatingButtons(JFrame ask, JPanel c) {
        boolean[] flag = {false, false};
        addTanks(ask, c, flag);
        addBullet(ask, c, flag);
    }

    private void creatingUserInfo(JPanel c, JLabel label, String text, int y) {
        label.setLocation(600, y);
        label.setFont(new Font(label.getFont().getName(), label.getFont().getStyle(), 20));
        label.setSize(250, 25);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText(text);
        label.setBackground(Color.BLACK);
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        c.add(label);
    }

    private void addTanks(JFrame ask, JPanel c, boolean[] flag) {
        File file = new File("src/game/IconsInGame/Farshid/Tank");
        int tmp = 0;
        for (String name : Objects.requireNonNull(file.list())) {
            JButton button = new JButton(new ImageIcon(file.getPath() + File.separator + name));
            button.setLocation(160 * (tmp % 2), 100 + 110 * (tmp++ / 2));
            button.setSize(150, 100);
            c.add(button);
            button.addActionListener(e -> {
                finalImage = new File(file.getPath() + File.separator + name);
                flag[0] = true;
                if (flag[1]) {
                    ask.setVisible(false);
                    send.doClick();
                }
            });
        }
    }

    private void addBullet(JFrame ask, JPanel c, boolean[] flag) {
        File file = new File("src/game/IconsInGame/Farshid/Bullet");
        int tmp = 0;// Reading bullet files and adding them to customizer frame
        for (String name : Objects.requireNonNull(file.list())) {
            JButton button = new JButton(new ImageIcon(file.getPath() + File.separator + name));
            button.setLocation(1250 - 40 * (tmp / 5), 100 + 55 * (tmp++ % 5));
            button.setSize(30, 45);
            c.add(button);
            button.addActionListener(e -> {
                finalBullet = new File(file.getPath() + File.separator + name);
                flag[1] = true;
                if (flag[0]) {
                    ask.setVisible(false);
                    send.doClick();
                }
            });
        }
    }

    private void initButtons() {
        send.addActionListener(e -> new SwingWorker<>() {

            @Override
            protected Object doInBackground() {
                Socket socket = null;
                try {
                    socket = new Socket("127.0.0.1", 1725);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    assert socket != null;
                    PrintStream printStream = new PrintStream(socket.getOutputStream());
                    printStream.println(finalImage.getPath());
                    printStream.println(finalBullet.getPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                setVisible(false);
                JoinGame joinGame = new JoinGame();
                joinGame.run();
            }
        }.execute());
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
