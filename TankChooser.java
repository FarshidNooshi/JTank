package game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Objects;

public class TankChooser extends JFrame {

    private File finalImage = new File("src/game/IconsInGame/Farshid/Tank/Icon.png");
    private File finalBullet = new File("src/game/IconsInGame/Farshid/Bullet/fireball2.png");
    private String username;
    private JButton send;

    public TankChooser(String username) throws IOException {
        send = new JButton();
        this.username = username;
    }

    public void run() {
        initButtons();

        int response = JOptionPane.showConfirmDialog(this, "Do you want to customize your tank ?");
        if (response == JOptionPane.YES_OPTION) {
            JFrame ask = new JFrame("Choose tank model");
            JPanel c = new MainPanel(new ImageIcon("src/game/IconsInGame/Farshid/background.png").getImage());
            JLabel logo = new JLabel(new ImageIcon("src/game/IconsInGame/Logo.png"));
            c.add(logo);
            logo.setLocation(300, 0);
            logo.setSize(700, 150);
            ask.setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
            // Getting the tanks images
            File file = new File("src/game/IconsInGame/Farshid/Tank");
            c.setLayout(null);
            int tmp = 0;
            ask.setExtendedState(Frame.MAXIMIZED_BOTH);
            ask.setResizable(false);
            // Creating buttons
            CreatingButtons(ask, c, file, tmp);
            ask.add(c);
            ask.setAlwaysOnTop(true);
            ask.setLocationRelativeTo(null);
            ask.setVisible(true);
        } else {
            send.doClick();
        }
    }

    private void CreatingButtons(JFrame ask, JPanel c, File file, int tmp) {
        boolean[] flag = {false, false};
        addTanks(ask, c, file, tmp, flag);
        addBullet(ask, c, flag);
    }

    private void addTanks(JFrame ask, JPanel c, File file, int tmp, boolean[] flag) {
        for (String name : Objects.requireNonNull(file.list())) {
            JButton button = new JButton(new ImageIcon(file.getPath() + File.separator + name));
            button.setLocation(160 * (tmp / 5), 100 + 110 * (tmp++ % 5));
            button.setSize(150, 100);
            c.add(button);
            File finalFile1 = file;
            button.addActionListener(e -> {
                finalImage = new File(finalFile1.getPath() + File.separator + name);
                flag[0] = true;
                if (flag[0] && flag[1]) {
                    ask.setVisible(false);
                    send.doClick();
                }
            });
        }
    }

    private void addBullet(JFrame ask, JPanel c, boolean[] flag) {
        File file;
        int tmp;// Reading bullet files and adding them to customizer frame
        file = new File("src/game/IconsInGame/Farshid/Bullet");
        tmp = 0;
        for (String name : Objects.requireNonNull(file.list())) {
            JButton button = new JButton(new ImageIcon(file.getPath() + File.separator + name));
            button.setLocation(1250 - 40 * (tmp / 5), 100 + 55 * (tmp++ % 5));
            button.setSize(30, 45);
            c.add(button);
            File finalFile = file;
            button.addActionListener(e -> {
                finalBullet = new File(finalFile.getPath() + File.separator + name);
                flag[1] = true;
                if (flag[1] && flag[0]) {
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
