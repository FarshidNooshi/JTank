package game.Server;

import game.Process.Bullet;
import game.Process.GameMap;
import game.Process.GameState;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private final GameMap gameMap;
    private Vector<User> playersVector;
    private boolean gameOver;
    private int numberOfPlayers;
    private CopyOnWriteArrayList<Bullet> bullets;
    private ExecutorService executorService;
    private ExecutorService clientsService;

    /**
     * The constructor of the game loop.
     * To create the frame of the game.
     */
    public GameLoop(GameMap gameMap, Vector<User> vector) {
        this.gameMap = gameMap;
        playersVector = vector;
        this.numberOfPlayers = vector.size();
        createStreams();
        initialize();
    }

    /**
     * This method will open the server side
     * streams.
     *
     */
    private void createStreams() {
        for (User u : playersVector) {
            try {
                u.out = new ObjectOutputStream(u.getClientSocket().getOutputStream());
                u.in = new ObjectInputStream(u.getClientSocket().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initialize() {
        // Creating the states in here
        for (User u : playersVector) {
            GameState state = new GameState();
            state.setLimits(gameMap.getNumberOfRows(), gameMap.getNumberOfColumns());
            u.setState(state);
        }
        gameMap.setPlaces(playersVector);
        // Giving the users their map and state
        for (User u : playersVector) {
            write(gameMap, u);
            write(u.getState(), u);
        }
    }

    /**
     * This must be called before the game loop starts.
     */
    public void init() {
        // TODO: 01-Agt-2020 need a loop for sending others status
        bullets = new CopyOnWriteArrayList<>();
        executorService = Executors.newCachedThreadPool();
        clientsService = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        for (User u : playersVector)
            clientsService.execute(new ClientHandler(u)); // Executing the clients

        while ((numberOfPlayers == 1 && !playersVector.get(0).getState().gameOver) || (playersVector.size() > 1)) { // onio ke gameOver shod az vector bendazim biroon
            Iterator<Bullet> iterator = bullets.iterator();
            //TODO: 03-08-2020 this is the problem of the bullets I think
            // We send the bullets that are still in the middle of updating
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                if (bullet.isAlive())
                    executorService.execute(bullet.getMover());
                else
                    iterator.remove();
            }
        }
        gameOver = true;
        clientsService.shutdownNow();
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
            state.width = (int) u.read();
            state.height = (int) u.read();

            while (!gameOver) {

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
                    if (state.shotFired) {
                        Bullet bullet = new Bullet(state.locX + state.width / 2, state.locY + state.height / 2, gameMap);
                        bullet.setDirections(state.direction());
                        bullets.add(bullet);
                    }
                    u.setState(state);
                    if (state.gameOver)
                        playersVector.remove(u); // Removing the looser ones
                }
                try {
                    u.out.reset(); // This is for sending the new state, it helps the syncing between client and server
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // giving the data
                write(bullets, u);
                write(playersVector, u);
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
