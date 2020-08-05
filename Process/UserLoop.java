package game.Process;

import game.Log;
import game.Server.MysteryBox;
import game.Server.User;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is for the user game loop
 * where we send the current user state
 * and we will get the other users
 * and will show the output.
 */
public class UserLoop extends Thread {
    // private fields
    public static final int FPS = 30;
    private User thisPlayerUser;
    private GameFrame canvas;
    private int rounds;

    /**
     * The main constructor of the UserLoop class.
     * @param user the current user
     */
    public UserLoop(User user) { thisPlayerUser = user; }

    /**
     * Starting the user loop of the game.
     */
    public void initialize() {
        try {
            thisPlayerUser.init(); // To open the user connection to the game
        } catch (IOException e) {
            e.printStackTrace();
        }
        rounds = thisPlayerUser.gameData.numberOfRounds;
    }

    @Override
    public void run() {
        while (rounds > 0) {
            // for waiting
            JOptionPane.showMessageDialog(canvas, "Please wait for the others " + thisPlayerUser.getUserName(), "Loading ...", JOptionPane.WARNING_MESSAGE);
            String in = String.valueOf(thisPlayerUser.read());
            //
            canvas = new GameFrame("Jtank", thisPlayerUser.getUserName(), thisPlayerUser.getImagePath());
            canvas.setVisible(true);
            canvas.initBufferStrategy();
            //
            Tank tank = new Tank(canvas.getImage().getWidth() / 4, canvas.getImage().getHeight() / 4); // Creating the user input
            canvas.addKeyListener(tank.getKeyListener());
            canvas.addMouseListener(tank.getMouseListener());
            canvas.addMouseMotionListener(tank.getMouseMotionListener());
            // Send the sizes
            thisPlayerUser.write(tank.width);
            thisPlayerUser.write(tank.height);
            // The game loop
            while (true) {
                try {
                    int inRoundStatus = (int) thisPlayerUser.read(); // We use this to check if we are getting inputs or not
                    if (inRoundStatus == -1)
                        break;
                } catch (NullPointerException | ClassCastException e) {
                    break;
                }
                long start = System.currentTimeMillis(); // This is for delay between server and client
                //
                thisPlayerUser.write(tank.keyUP); // Giving the data
                thisPlayerUser.write(tank.keyDOWN);
                thisPlayerUser.write(tank.keyLEFT);
                thisPlayerUser.write(tank.keyRIGHT);
                thisPlayerUser.write(tank.mousePress);
                thisPlayerUser.write(tank.mouseX);
                thisPlayerUser.write(tank.mouseY);
                thisPlayerUser.write(tank.shotFired);
                // receiving the data
                CopyOnWriteArrayList<Bullet> bullets = (CopyOnWriteArrayList<Bullet>) thisPlayerUser.read();
                canvas.setBullets(bullets); // Get the bullets and the users
                CopyOnWriteArrayList<MysteryBox> boxes = (CopyOnWriteArrayList<MysteryBox>) thisPlayerUser.read();
                canvas.setBoxes(boxes);
                CopyOnWriteArrayList<User> users = (CopyOnWriteArrayList<User>) thisPlayerUser.read();
                GameMap gameMap = (GameMap) thisPlayerUser.read();
                canvas.setGameMap(gameMap);
                //
                if (gameMap != null)
                    canvas.render(users); // do the rendering
                //
                long delay = (1000 / FPS) - (System.currentTimeMillis() - start); // This is for handling the delays
                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            canvas.setVisible(false);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rounds--;
        }
        // Enter into the game setting again
        System.exit(-1);
    }
}
