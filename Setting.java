package game;

import game.Process.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Setting {
    private static String[] gameModes = {"Death Match", "Teams Battle"};
    private static String[] tankSpeeds = {"4", "8", "12"};
    private static String[] bulletSpeeds = {"8", "16", "32"};
    private static String[] health = {"Low", "Half", "Full"};
    protected static JFrame frame = new JFrame("J Tank Trouble - Setting");
    private static JLabel gameMode = new JLabel("Game mode : ");
    private static JComboBox<String> modeInput = new JComboBox<>(gameModes);
    private static JLabel tankSpeed = new JLabel("Tanks speed : ");
    private static JComboBox<String> tankSpeedInput = new JComboBox<>(tankSpeeds);
    private static JLabel bulletSpeed = new JLabel("Bullets speed : ");
    private static JComboBox<String> bulletSpeedInput = new JComboBox<>(bulletSpeeds);
    private static JLabel wallDamage = new JLabel("Walls health : ");
    private static JComboBox<String> wallDamageInput = new JComboBox<>(health);
    private static JLabel tankDamage = new JLabel("Tanks health : ");
    private static JComboBox<String> tankDamageInput = new JComboBox<>(health);
    private static JLabel minPeople = new JLabel("Minimum players : ");
    private static JLabel minPeopleInput = new JLabel(String.valueOf(1));
    private static JLabel maxPeople = new JLabel("Maximum players : ");
    private static JLabel maxPeopleInput = new JLabel(String.valueOf(10));
    private static JButton decreaseMin = new JButton("<<");
    private static JButton increaseMin = new JButton(">>");
    private static JButton decreaseMax = new JButton("<<");
    private static JButton increaseMax = new JButton(">>");
    private static JButton cancel = new JButton("Cancel");
    private static JButton send = new JButton("Go");

    public static void run() {

        frame.setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
        frame.setPreferredSize(new Dimension(800, 500));
        frame.setLocation(250, 100);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        init();

        Runnable r = () -> {
            JPanel c = new JPanel();
            try {
                c = new MainPanel(ImageIO.read(new File("src/game/IconsInGame/Farshid/background.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert c != null;
            c.setLayout(null);
            gameMode.setLocation(250, 50);
            modeInput.setLocation(450, 50);
            tankSpeed.setLocation(250, 100);
            tankSpeedInput.setLocation(450, 100);
            bulletSpeed.setLocation(250, 150);
            bulletSpeedInput.setLocation(450, 150);
            wallDamage.setLocation(250, 200);
            wallDamageInput.setLocation(450, 200);
            tankDamage.setLocation(250, 250);
            tankDamageInput.setLocation(450, 250);
            minPeople.setLocation(250, 300);
            decreaseMin.setLocation(450, 300);
            minPeopleInput.setLocation(525, 300);
            increaseMin.setLocation(575, 300);
            maxPeople.setLocation(250, 350);
            decreaseMax.setLocation(450, 350);
            maxPeopleInput.setLocation(525, 350);
            increaseMax.setLocation(575, 350);
            cancel.setLocation(250, 400);
            send.setLocation(450, 400);
            c.add(gameMode);
            c.add(modeInput);
            c.add(tankSpeed);
            c.add(tankSpeedInput);
            c.add(bulletSpeed);
            c.add(bulletSpeedInput);
            c.add(wallDamage);
            c.add(wallDamageInput);
            c.add(tankDamage);
            c.add(tankDamageInput);
            c.add(minPeople);
            c.add(decreaseMin);
            c.add(minPeopleInput);
            c.add(increaseMin);
            c.add(maxPeople);
            c.add(decreaseMax);
            c.add(maxPeopleInput);
            c.add(increaseMax);
            c.add(cancel);
            c.add(send);
            frame.add(c);
            frame.pack();
            frame.setVisible(true);
        };
        SwingUtilities.invokeLater(r);
    }

    private static void init() {
        gameMode.setSize(100,25);
        gameMode.setOpaque(true);
        gameMode.setBackground(Color.GRAY);
        modeInput.setSize(100, 25);
        tankSpeed.setSize(100, 25);
        tankSpeed.setOpaque(true);
        tankSpeed.setBackground(Color.GRAY);
        tankSpeedInput.setSize(100, 25);
        bulletSpeed.setSize(100, 25);
        bulletSpeed.setOpaque(true);
        bulletSpeed.setBackground(Color.GRAY);
        bulletSpeedInput.setSize(100, 25);
        wallDamage.setSize(100, 25);
        wallDamage.setOpaque(true);
        wallDamage.setBackground(Color.GRAY);
        wallDamageInput.setSize(100, 25);
        tankDamage.setSize(100, 25);
        tankDamage.setOpaque(true);
        tankDamage.setBackground(Color.GRAY);
        tankDamageInput.setSize(100, 25);
        minPeople.setSize(150, 25);
        minPeople.setOpaque(true);
        minPeople.setBackground(Color.GRAY);
        minPeopleInput.setSize(50, 25);
        maxPeople.setSize(150, 25);
        maxPeople.setOpaque(true);
        maxPeople.setBackground(Color.GRAY);
        maxPeopleInput.setSize(50, 25);
        decreaseMin.setSize(new Dimension(50, 25));
        decreaseMax.setSize(new Dimension(50, 25));
        increaseMin.setSize(new Dimension(50, 25));
        increaseMax.setSize(new Dimension(50, 25));
        cancel.setSize(new Dimension(100, 25));
        cancel.setHorizontalTextPosition(SwingConstants.CENTER);
        send.setSize(new Dimension(100, 25));
        send.setHorizontalTextPosition(SwingConstants.CENTER);
        decreaseMin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int target = Integer.parseInt(minPeopleInput.getText());
                if (target != 0)
                    minPeopleInput.setText(String.valueOf(--target));
            }
        });
        increaseMin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int target = Integer.parseInt(minPeopleInput.getText());
                int limit = Integer.parseInt(maxPeopleInput.getText());
                if (target < limit)
                    minPeopleInput.setText(String.valueOf(++target));
                else {
                    maxPeopleInput.setText(String.valueOf(++target));
                    minPeopleInput.setText(String.valueOf(target));
                }
            }
        });
        decreaseMax.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int target = Integer.parseInt(maxPeopleInput.getText());
                int limit = Integer.parseInt(minPeopleInput.getText());
                if (target > limit)
                    maxPeopleInput.setText(String.valueOf(--target));
                else if (target != 0) {
                    maxPeopleInput.setText(String.valueOf(--target));
                    minPeopleInput.setText(String.valueOf(target));
                }
            }
        });
        increaseMax.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int target = Integer.parseInt(maxPeopleInput.getText());
                maxPeopleInput.setText(String.valueOf(++target));
            }
        });
        initButtons();
    }

    private static void initButtons() {
        send.addActionListener(e -> new SwingWorker<>(){

            @Override
            protected Object doInBackground() throws Exception {
                //TODO: 28-jul-2020 inja bayad setting bazi be server ersal beshe
                return null;
            }

            @Override
            protected void done() {
                frame.setVisible(false);
                Main.startTheGame(); // Getting into the game
            }
        }.execute());
        cancel.addActionListener(e -> new SwingWorker<>(){

            @Override
            protected Object doInBackground() throws Exception {
                //TODO: 28-jul-2020 baray dokmeh cancel ham bayad yek method bezamin
                return null;
            }

            @Override
            protected void done() {
                frame.setVisible(false);
                Main.startTheGame();
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
