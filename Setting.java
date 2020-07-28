package game;

import game.Process.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

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
    private static JButton decreaseMin = new JButton("<");
    private static JButton increaseMin = new JButton(">");
    private static JButton decreaseMax = new JButton("<");
    private static JButton increaseMax = new JButton(">");
    private static JButton cancel = new JButton("Cancel");
    private static JButton send = new JButton("Go");

    public static void run() {
        System.out.println("Got here");
        frame.setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
        frame.setPreferredSize(new Dimension(750, 500));
        frame.setLocation(300, 100);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel c = new JPanel();
        c.setLayout(null);
        gameMode.setLocation(75, 10);
        modeInput.setLocation(225, 10);
        tankSpeed.setLocation(75, 20);
        tankSpeedInput.setLocation(225, 20);
        bulletSpeed.setLocation(75, 30);
        bulletSpeedInput.setLocation(225, 30);
        wallDamage.setLocation(75, 40);
        wallDamageInput.setLocation(225, 40);
        tankDamage.setLocation(75, 50);
        tankDamageInput.setLocation(225, 50);
        minPeople.setLocation(75, 60);
        decreaseMin.setLocation(150 , 60);
        minPeopleInput.setLocation(200, 60);
        increaseMin.setLocation(250, 60);
        maxPeople.setLocation(75, 70);
        decreaseMax.setLocation(150, 70);
        maxPeopleInput.setLocation(200, 70);
        increaseMax.setLocation(250, 70);
        cancel.setLocation(75, 80);
        send.setLocation(225, 80);
        init();
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
    }

    private static void init() {
        System.out.println("Got here in init");
        decreaseMin.setPreferredSize(new Dimension(25, 10));
        decreaseMax.setPreferredSize(new Dimension(25, 10));
        increaseMin.setPreferredSize(new Dimension(25, 10));
        increaseMax.setPreferredSize(new Dimension(25, 10));
        cancel.setPreferredSize(new Dimension(50, 10));
        cancel.setHorizontalTextPosition(SwingConstants.CENTER);
        send.setPreferredSize(new Dimension(50, 10));
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
                if (target <= limit)
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
                if (target >= limit)
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

        System.out.println("Got here in initButtons");

        send.addActionListener(e -> new SwingWorker<>(){

            @Override
            protected Object doInBackground() throws Exception {
                //TODO: 28-jul-2020 inja bayad setting bazi be server ersal beshe
                return null;
            }

            @Override
            protected void done() {
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
                EventQueue.invokeLater(Log::run);
            }
        }.execute());
    }
}
