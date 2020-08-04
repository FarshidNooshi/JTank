package game.Server;

import game.Process.Bullet;
import game.Process.GameMap;
import game.Process.GameState;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A very simple structure for the main game loop.
 */
public class GameLoop implements Runnable {
    // Private fields
    public static final int FPS = 25; // Bullet delay handler
    private boolean gameOver;
    private int numberOfPlayers;
    private final GameMap gameMap;
    private GameData gameData;
    private Vector<User> playersVector;
    private CopyOnWriteArrayList<Bullet> bullets;
    private ExecutorService executorService, clientsService;

    /**
     * The constructor of the game loop.
     * To create the frame of the game.
     */
    public GameLoop(GameMap gameMap, Vector<User> vector, GameData gameData) {
        //
        this.gameMap = gameMap;
        playersVector = vector;
        //
        this.numberOfPlayers = gameData.numberOfPeople;
        this.gameData = gameData;
        //
        initialize();
        start();
    }

    private void start() {
        for (User u : playersVector)
            u.write("start");
    }

    private void initialize() {
        // Creating the states in here
        for (User u : playersVector) {
            GameState state = new GameState(gameMap.locationController);
            state.speed = gameData.tankSpeed;
            state.setLimits(gameMap.getNumberOfRows(), gameMap.getNumberOfColumns());
            u.setState(state);
        }
        gameMap.setPlaces(playersVector);
    }

    /**
     * This must be called before the game loop starts.
     */
    public void init() {
        bullets = new CopyOnWriteArrayList<>();
        executorService = Executors.newCachedThreadPool();
        clientsService = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        //
        for (User u : playersVector)
            clientsService.execute(new ClientHandler(u)); // Executing the clients
        clientsService.execute(new TankBullet());
        //
        while (!gameOver) {
            long start = System.currentTimeMillis(); // Delay handling
            //
            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                if (bullet.isAlive())
                    executorService.execute(bullet.getMover());
                else
                    bullets.remove(bullet);
            }
            // TODO: 04-08-2020 handle bullets hit tanks
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
        //
        executorService.shutdownNow();
        clientsService.shutdownNow();
    }

    // This is for bullet and players handling
    private class TankBullet implements Runnable {
        @Override
        public void run() {
            while (!gameOver) {
                for (User u : playersVector) {
                    //
                    GameState state = u.getState();
                    //
                    Iterator<Bullet> iterator = bullets.iterator();
                    while (iterator.hasNext()) {
                        //
                        Bullet b = iterator.next();
                        //
                        if (b.hitTheTank(state.locX, state.locY, state.width, state.height)) {
                            //
                            b.isAlive = false;
                            state.gameOver = true;
                            //
                            numberOfPlayers--;
                            if (numberOfPlayers <= 1)
                                gameOver = true;
                            //
                            u.updateDataBox();
                            break;
                        }
                    }
                }
            }
        }
    }

    // The client runnable
    private class ClientHandler implements Runnable {
        // The user
        private User u;

        /**
         * The class constructor
         * @param u each user uses its own client handler in server
         */
        public ClientHandler(User u) {
            this.u = u;
        }

        @Override
        public void run() {
            GameState state = u.getState();
            // Receiving the width and height
            state.width = (int) u.read();
            state.height = (int) u.read();
            // Server client game loop
            while (!gameOver) {
                // Getting the updated data
                state.keyUP = (boolean) u.read();
                state.keyDOWN = (boolean) u.read();
                state.keyLEFT = (boolean) u.read();
                state.keyRIGHT = (boolean) u.read();
                state.mousePress = (boolean) u.read();
                state.mouseX = (int) u.read();
                state.mouseY = (int) u.read();
                state.shotFired = (boolean) u.read();
                // Updating while the player is on the game
                if (!state.gameOver) {
                    state.update();
                    u.updateDataBox();
                    if (state.shotFired) {
                        //TODO 03-08-2020: need to dedicate the image of the bullets
                        Bullet bullet = new Bullet(state.locX + state.width / 2, state.locY + state.height / 2, gameMap, gameData.bulletSpeed);
                        bullet.setDirections(state.direction());
                        bullets.add(bullet);
                    }
                    u.setState(state);
                }
                try {
                    u.out.reset(); // This is for sending the new state, it helps the syncing between client and server
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // giving the data
                write(bullets, u);
                write(playersVector, u);
                write(gameMap, u);
            }
        }
    }

    private Object read(User u) {
        try {
            return u.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void write(Object object, User u) {
        try {
            u.write(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
