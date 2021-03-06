package game;

import game.Process.UserLoop;
import game.Server.GameData;
import game.Server.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This class gets the current games on the
 * server that are available and show them to the
 * client to choose a game or create on.
 */
public class JoinGame extends JFrame {
    // Private fields
    private JButton send;
    private JButton create;
    private Socket connectionSocket;
    private ArrayList<GameData> data;
    private GameData finalChose;
    private JPanel c;
    private Setting setting;

    /**
     * The constructor of the on server games.
     */
    JoinGame(Setting setting) {
        this.setting = setting;
        //
        setTitle("JTank Trouble - Game Choosing");
        setSize(new Dimension(750, 500));
        setLocation(390, 130);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        //
        try {
            c = new MainPanel(ImageIO.read(new File("src/game/IconsInGame/Farshid/background.png")));
            add(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        c.setLayout(null);
        //
    }

    public void run() {
        init();
        frameCreate();
        initButtons();
        //
        setVisible(true);
    }

    private void init() {
        //
        send = new JButton("Send");
        create = new JButton("Create");
        //
        try {
            connectionSocket = new Socket("127.0.0.1", 1724);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //
        try {
            data = read(); // Receiving the data
            if (data.size() > 0)
                finalChose = data.get(0);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<GameData> read() throws IOException, ClassNotFoundException {
        Object obj = new ObjectInputStream(connectionSocket.getInputStream()).readObject();
        ArrayList<GameData> ret = new ArrayList<>();
        if (obj.getClass() == ret.getClass())
            //noinspection unchecked
            ret = (ArrayList<GameData>) obj;
        else
            System.err.println("Unknown object received.");
        return ret;
    }

    private void frameCreate() {
        //
        int counter = 1;
        for (GameData d : data) {
            JButton jButton = new JButton();
            jButton.setSize(new Dimension(700, 30));
            jButton.setBackground(new Color(30, 90, 220));
            jButton.setForeground(Color.WHITE);
            jButton.setText(d.toString());
            jButton.setLocation(25, counter * 20);
            jButton.addActionListener(e -> finalChose = d);
            c.add(jButton);
            counter++;
        }
        //
        create.setSize(new Dimension(100, 20));
        send.setSize(new Dimension(100, 20));
        create.setLocation(225, ++counter * 20);
        send.setLocation(425, counter * 20);
        send.setBackground(Color.BLACK);
        create.setBackground(Color.BLACK);
        send.setForeground(Color.white);
        create.setForeground(Color.white);
        c.add(send);
        c.add(create);
    }

    private void initButtons() {
        //
        send.addActionListener(e -> new SwingWorker<>() {

            @Override
            protected Object doInBackground() throws Exception {
                connectionSocket.getOutputStream().write(1);
                if (finalChose == null)
                    return -1;
                return 0;
            }

            @Override
            protected void done() {
                setVisible(false);
                try {
                    //
                    User u;
                    Socket gameSocket = new Socket("127.0.0.1", 1723);
                    u = (User) new ObjectInputStream(gameSocket.getInputStream()).readObject();
                    //
                    int result = (int) get();
                    if (result == -1)
                        System.exit(-1);
                    else {
                        new DataOutputStream(gameSocket.getOutputStream()).writeInt(finalChose.port);
                        //
                        u.gameData = finalChose;
                        //
                        UserLoop userLoop = new UserLoop(u);
                        userLoop.initialize();
                        userLoop.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute());
        //
        create.addActionListener(e -> new SwingWorker<>() {

            @Override
            protected Object doInBackground() throws Exception {
                connectionSocket.getOutputStream().write(0);
                return null;
            }

            @Override
            protected void done() {
                setVisible(false);
                setting.run();
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
