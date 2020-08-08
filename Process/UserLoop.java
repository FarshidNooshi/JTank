package game.Process;

import game.Log;
import game.ResultShower;
import game.Server.MysteryBox;
import game.Server.User;
import game.WaitingPage;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is for the user game loop
 * where we send the current user state
 * and we will get the other users
 * and will show the output.
 */
public class UserLoop extends Thread {
    // private fields
    private static final int FPS = 30;
    private User thisPlayerUser;
    private int rounds;

    /**
     * The main constructor of the UserLoop class.
     *
     * @param user the current user
     */
    public UserLoop(User user) {
        thisPlayerUser = user;
    }

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
        while (rounds-- > 0) {
            // for waiting
            WaitingPage waitingPage = new WaitingPage();
            waitingPage.start();
            //noinspection ResultOfMethodCallIgnored
            String.valueOf(thisPlayerUser.read());
            waitingPage.shutDown();
            //
            GameFrame canvas = new GameFrame("Jtank", thisPlayerUser.getUserName(), thisPlayerUser.getImagePath());
            canvas.setVisible(true);
            canvas.initBufferStrategy();
            Tank tank = new Tank(canvas.getImage().getWidth() / 4, canvas.getImage().getHeight() / 4); // Creating the user input
            canvas.addKeyListener(tank.getKeyListener());
            canvas.addMouseListener(tank.getMouseListener());
            canvas.addMouseMotionListener(tank.getMouseMotionListener());
            thisPlayerUser.write(tank.width); // Send the tank sizes
            thisPlayerUser.write(tank.height);
            thisPlayerUser.teamNumber = (int) thisPlayerUser.read();
            while (true) {
                try {
                    int inRoundStatus = (int) thisPlayerUser.read(); // We use this to check if we are getting inputs or not
                    if (inRoundStatus == -1)
                        break;
                } catch (NullPointerException | ClassCastException e) {
                    break;
                }
                long start = System.currentTimeMillis(); // This is for delay between server and client
                // Giving the data
                GivingTheData(tank);
                // receiving the data
                CopyOnWriteArrayList<Bullet> bullets = (CopyOnWriteArrayList<Bullet>) thisPlayerUser.read();
                canvas.setBullets(bullets); // Get the bullets and the users
                CopyOnWriteArrayList<MysteryBox> boxes = (CopyOnWriteArrayList<MysteryBox>) thisPlayerUser.read();
                canvas.setBoxes(boxes);
                CopyOnWriteArrayList<User> users = (CopyOnWriteArrayList<User>) thisPlayerUser.read();
                GameMap gameMap = (GameMap) thisPlayerUser.read();
                canvas.setGameMap(gameMap);
                assert gameMap != null;
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
            String winner = (String) thisPlayerUser.read();
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            canvas.setVisible(false);
            ResultShower resultShower = new ResultShower();
            if (thisPlayerUser.isTeamMatch) {
                if (thisPlayerUser.teamNumber == 1 && winner.equals("Blue team") || thisPlayerUser.teamNumber == 2 && winner.equals("Red team"))
                    resultShower.start(winner, 1);
                else
                    resultShower.start(winner, 0);
            }
            else {
                if (thisPlayerUser.getUserName().equals(winner))
                    resultShower.start(winner, 1);
                else
                    resultShower.start(winner, 0);
            }
            try {
                Thread.sleep(4000);
                thisPlayerUser.out.flush();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                resultShower.shutDown();
            }
        }
        // Enter into the game setting again
        Restart restart = new Restart();
        restart.start();
    }

    private void GivingTheData(Tank tank) {
        thisPlayerUser.write(tank.keyUP);
        thisPlayerUser.write(tank.keyDOWN);
        thisPlayerUser.write(tank.keyLEFT);
        thisPlayerUser.write(tank.keyRIGHT);
        thisPlayerUser.write(tank.mousePress);
        thisPlayerUser.write(tank.mouseX);
        thisPlayerUser.write(tank.mouseY);
        thisPlayerUser.write(tank.shotFired);
    }

    private static class Restart extends Thread {
        @Override
        public void run() {
            EventQueue.invokeLater(new Log()::run);
        }
    }
}
