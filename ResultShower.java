package game;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * This class shows the result of the round.
 */
public class ResultShower {
    // private fields
    private JFrame jFrame;
    private JPanel jPanel;
    private JLabel jLabel;
    private JLabel jLabel2;
    private String[] winners = {"Victory is yours", "You won", "Well done lad"};
    private String[] looser = {"Battle lost", "Mission failed", "You lost"};
    private Random random;

    public ResultShower() {
        jFrame = new JFrame("Round finished");
        jPanel = new JPanel(new BorderLayout());
        jLabel = new JLabel();
        jLabel2 = new JLabel();
        random = new Random();
    }

    public void start(String userName, int status) {
        jFrame.setSize(300,200);
        jFrame.setLocation(650,300);
        jFrame.setResizable(false);
        jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        jLabel.setSize(100,100);
        jLabel2.setPreferredSize(new Dimension(100,50));
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setForeground(Color.WHITE);
        jLabel.setFont(new Font(jLabel.getFont().getName(), 22, 30));
        jLabel2.setForeground(Color.WHITE);
        jPanel.setSize(250,100);
        if (status == 1) {
            jPanel.setBackground(new Color(30, 100, 60));
            jLabel.setText(winners[random.nextInt(3)]);
        }
        else {
            jPanel.setBackground(new Color(200, 50, 60));
            jLabel.setText(looser[random.nextInt(3)]);
        }
        jLabel2.setText("The winner is " + userName);
        jPanel.add(jLabel);
        jPanel.add(jLabel2, BorderLayout.SOUTH);
        jFrame.add(jPanel);
        jFrame.setVisible(true);
    }

    public void shutDown() {
        jFrame.setVisible(false);
    }
}
