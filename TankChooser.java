package game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class TankChooser extends JFrame{

    private File finalImage = null;
    private File finalBullet = null;
    private BufferedImage image = null;
    private BufferedImage bullet = null;
    private String username;
    private JButton send;
    private int counter = 0;

    public TankChooser(String username) {

        send = new JButton();
        this.username = username;
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
            // Creating buttons
            for (String name : Objects.requireNonNull(file.list())) {
                JButton button = new JButton(new ImageIcon(file.getPath() + File.separator + name));
                button.setLocation(160 * (tmp / 5), 100 + 110 * (tmp++ % 5));
                button.setSize(150, 100);
                c.add(button);
                File finalFile1 = file;
                finalImage = finalFile1;
                button.addActionListener(e -> {
                    try {
                        image = ImageIO.read(new File(finalFile1.getPath() + File.separator + name));
                        counter++;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (counter == 2) {
                        ask.setVisible(false);
                        send.doClick();
                    }
                });
            }
            // Reading bullet files and adding them to customizer frame
            file = new File("src/game/IconsInGame/Farshid/Bullet");
            tmp = 0;
            for (String name : Objects.requireNonNull(file.list())) {
                JButton button = new JButton(new ImageIcon(file.getPath() + File.separator + name));
                button.setLocation(1250 - 40 * (tmp / 5), 100 + 55 * (tmp++ % 5));
                button.setSize(30, 45);
                c.add(button);
                File finalFile = file;
                finalBullet = finalFile;
                button.addActionListener(e -> {
                    try {
                        bullet = ImageIO.read(new File(finalFile.getPath() + File.separator + name));
                        counter++;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (counter == 2) {
                        ask.setVisible(false);
                        send.doClick();
                    }
                });
            }
            ask.add(c);
            ask.setAlwaysOnTop(true);
            ask.setLocationRelativeTo(null);
            ask.setVisible(true);
        }
    }

    private void initButtons() {
        send.addActionListener(e -> new SwingWorker<>(){

            @Override
            protected Object doInBackground() throws Exception {
                Socket socket = new Socket("127.0.0.1", 1725);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.println(username);
                printWriter.println(finalImage.getPath());
                printWriter.println(finalBullet.getPath());
                return null;
            }

            @Override
            protected void done() {
                Setting.run();
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
