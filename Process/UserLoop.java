package game.Process;

import game.Server.User;

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
    private Tank tank;

    /**
     * The main constructor of the UserLoop class.
     * @param user the current user
     */
    public UserLoop(User user) {

        thisPlayerUser = user;

        try {
            thisPlayerUser.init(); // To open the user connection to the game
        } catch (IOException e) {
            e.printStackTrace();
        }

        // creating the output frame
        canvas = new GameFrame("Jtank");
        canvas.setGameMap(thisPlayerUser.getGameMap());
        canvas.setVisible(true);
        canvas.initBufferStrategy();
        thisPlayerUser.getState().setLimits(canvas.getGameMap().getNumberOfRows(), canvas.getGameMap().getNumberOfColumns());
        thisPlayerUser.getState().width = canvas.getImage().getWidth() / 8; // Setting the width and the height
        thisPlayerUser.getState().height = canvas.getImage().getHeight() / 8;
        tank = new Tank(thisPlayerUser.getState().width, thisPlayerUser.getState().height);
        canvas.addKeyListener(tank.getKeyListener()); // Updating the listeners
        canvas.addMouseListener(tank.getMouseListener());
        canvas.addMouseMotionListener(tank.getMouseMotionListener());
    }

    @Override
    public void run() {

        // The game loop
        thisPlayerUser.write(thisPlayerUser.getState().width);
        thisPlayerUser.write(thisPlayerUser.getState().height);

        while (!thisPlayerUser.getState().gameOver) {

            long start = System.currentTimeMillis(); // This is for delay between server and client

            thisPlayerUser.write(tank.keyUP); // Giving the data
            thisPlayerUser.write(tank.keyDOWN);
            thisPlayerUser.write(tank.keyLEFT);
            thisPlayerUser.write(tank.keyRIGHT);
            thisPlayerUser.write(tank.mousePress);
            thisPlayerUser.write(tank.mouseX);
            thisPlayerUser.write(tank.mouseY);
            thisPlayerUser.write(tank.shotFired);

            canvas.setBullets((CopyOnWriteArrayList<Bullet>) thisPlayerUser.read()); // Get the bullets and the users
            Vector<User> users = (Vector<User>) thisPlayerUser.read();

            canvas.render(users); // do the rendering

            long delay = (1000 / FPS) - (System.currentTimeMillis() - start); // This is for handling the delays
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
