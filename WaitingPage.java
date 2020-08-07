package game;

import javax.swing.*;
import java.awt.*;

/**
 * This class shows a frame to the player
 * says wait for the other players.
 */
public class WaitingPage {
    // Private fields
    private JFrame jFrame;
    private JPanel jPanel;
    private JLabel jLabel;
    private String[] strings = {".","..","...","....",".....","......"};

    public WaitingPage() {
        jFrame = new JFrame("Wait for others");
        jPanel = new JPanel(new BorderLayout());
        jLabel = new JLabel();
    }

    public void start(){
        jFrame.setSize(300,100);
        jFrame.setLocation(650,300);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        jLabel.setSize(100,100);
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setText("Loading");
        jLabel.setForeground(Color.WHITE);
        jPanel.setSize(250,100);
        jPanel.add(jLabel);
        jPanel.setBackground(new Color(200,50,60));
        jFrame.add(jPanel);
        jFrame.setVisible(true);
        Changer changer = new Changer();
        changer.start();
    }

    public void shutDown() {
        jFrame.setVisible(false);
    }

    private class Changer extends Thread {
        @Override
        public void run() {
            int index = 0;
            while (jFrame.isVisible()) {
                jLabel.setText("Loading " + strings[index++ % strings.length]);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
