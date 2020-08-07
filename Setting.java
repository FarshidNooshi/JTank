package game;

import game.Process.UserLoop;
import game.Server.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;

/**
 * This is the setting menu where user
 * creates a game and will send it to the server.
 */
class Setting {
    // Private fields
    private static User u = null;
    private static Socket connectionSocket;
    private static String[] gameModes = {"Death Match", "League"};
    private static String[] numberOfRounds = {"1", "3", "5", "8", "10"};
    private static String[] tankSpeeds = {"3", "5", "6"};
    private static String[] bulletSpeeds = {"5", "8", "10"};
    private static String[] health = {"Low", "Half", "Full"};
    private static JFrame frame = new JFrame("J Tank Trouble - Setting");
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
    private static JLabel numberOfPeople = new JLabel("Number Of players : ");
    private static JLabel numberOfPeopleInput = new JLabel(String.valueOf(2));
    private static JButton decreaseNum = new JButton("<<");
    private static JButton increaseNum = new JButton(">>");
    private static JLabel rounds = new JLabel("Rounds : ");
    private static JComboBox<String> roundsInput = new JComboBox<>(numberOfRounds);
    private static JButton cancel = new JButton("Cancel");
    private static JButton send = new JButton("Go");

    /**
     * This method will build the setting frame
     * and will display it to the user.
     */
    static void run() {
        frame.setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
        frame.setPreferredSize(new Dimension(800, 500));
        frame.setLocation(350, 130);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        init();
        // Creating the setting frame
        Runnable r = () -> {
            JPanel c = new JPanel();
            try {
                c = new MainPanel(ImageIO.read(new File("src/game/IconsInGame/Farshid/background.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            c.setLayout(null);

            setLocations();
            addComponents(c);

            frame.add(c);
            frame.pack();
            frame.setVisible(true);
        };
        SwingUtilities.invokeLater(r);
    }

    private static void setLocations() {
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
        numberOfPeople.setLocation(250, 300);
        decreaseNum.setLocation(450, 300);
        numberOfPeopleInput.setLocation(525, 300);
        increaseNum.setLocation(575, 300);
        rounds.setLocation(250, 350);
        roundsInput.setLocation(450, 350);
        cancel.setLocation(250, 400);
        send.setLocation(450, 400);
    }

    private static void addComponents(JPanel c) {
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
        c.add(numberOfPeople);
        c.add(decreaseNum);
        c.add(numberOfPeopleInput);
        c.add(increaseNum);
        c.add(rounds);
        c.add(roundsInput);
        c.add(cancel);
        c.add(send);
    }

    private static void init() {
        initSizes(gameMode, modeInput, tankSpeed, tankSpeedInput);
        initSizes(bulletSpeed, bulletSpeedInput, wallDamage, wallDamageInput);
        iniSizes();
        setColors();

        addListeners();

        initButtons();
    }

    private static void addListeners() {
        decreaseNum.addActionListener(e -> {
            int target = Integer.parseInt(numberOfPeopleInput.getText());
            if (target != 2)
                numberOfPeopleInput.setText(String.valueOf(--target));
        });
        increaseNum.addActionListener(e -> {
            int target = Integer.parseInt(numberOfPeopleInput.getText());
            numberOfPeopleInput.setText(String.valueOf(++target));
        });
    }

    private static void iniSizes() {
        tankDamage.setSize(100, 25);
        tankDamage.setForeground(Color.BLACK);
        tankDamageInput.setSize(100, 25);
        numberOfPeople.setSize(150, 25);
        numberOfPeople.setForeground(Color.BLACK);
        numberOfPeopleInput.setSize(50, 25);
        decreaseNum.setSize(new Dimension(50, 25));
        increaseNum.setSize(new Dimension(50, 25));
        rounds.setSize(new Dimension(100, 25));
        rounds.setForeground(Color.BLACK);
        roundsInput.setSize(100, 25);
        cancel.setSize(new Dimension(100, 25));
        cancel.setHorizontalTextPosition(SwingConstants.CENTER);
        send.setSize(new Dimension(100, 25));
        send.setHorizontalTextPosition(SwingConstants.CENTER);
    }

    private static void setColors() {
        send.setForeground(Color.white);
        cancel.setForeground(Color.white);
        modeInput.setForeground(Color.white);
        increaseNum.setForeground(Color.white);
        roundsInput.setForeground(Color.white);
        decreaseNum.setForeground(Color.white);
        tankSpeedInput.setForeground(Color.white);
        tankDamageInput.setForeground(Color.white);
        wallDamageInput.setForeground(Color.white);
        bulletSpeedInput.setForeground(Color.white);
        send.setBackground(Color.BLACK);
        cancel.setBackground(Color.BLACK);
        modeInput.setBackground(Color.BLACK);
        decreaseNum.setBackground(Color.BLACK);
        increaseNum.setBackground(Color.BLACK);
        roundsInput.setBackground(Color.BLACK);
        tankSpeedInput.setBackground(Color.BLACK);
        tankDamageInput.setBackground(Color.BLACK);
        wallDamageInput.setBackground(Color.BLACK);
        bulletSpeedInput.setBackground(Color.BLACK);
    }

    private static void initSizes(JLabel bulletSpeed, JComboBox<String> bulletSpeedInput, JLabel wallDamage, JComboBox<String> wallDamageInput) {
        bulletSpeed.setSize(100, 25);
        bulletSpeed.setForeground(Color.BLACK);
        bulletSpeedInput.setSize(100, 25);
        wallDamage.setSize(100, 25);
        wallDamage.setForeground(Color.BLACK);
        wallDamageInput.setSize(100, 25);
    }

    private static void initButtons() {
        send.addActionListener(e -> new SwingWorker<>() {

            @Override
            protected Object doInBackground() {
                try {
                    PrintStream out = new PrintStream(connectionSocket.getOutputStream());
                    out.println(numberOfPeopleInput.getText());
                    out.println(modeInput.getSelectedItem());
                    out.println(bulletSpeedInput.getSelectedItem());
                    out.println(tankSpeedInput.getSelectedItem());
                    out.println(wallDamageInput.getSelectedIndex() + 1);
                    out.println(tankDamageInput.getSelectedIndex() + 1);
                    if ("League".equals(modeInput.getSelectedItem()))
                        out.println(roundsInput.getSelectedItem());
                    else
                        out.println(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                frame.setVisible(false);
                try (ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream())) {
                    u = (User) in.readObject();
                    assert u != null;
                    UserLoop userLoop = new UserLoop(u);
                    userLoop.initialize();
                    userLoop.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute());
        cancel.addActionListener(e -> new SwingWorker<>() {

            @Override
            protected Object doInBackground() {
                return null;
            }

            @Override
            protected void done() {
                System.exit(0);
            }
        }.execute());
    }


    static void setConnectionSocket(Socket connectionSocket) {
        Setting.connectionSocket = connectionSocket;
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
