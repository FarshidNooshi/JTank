package game.Server;

import game.Process.Bullet;
import game.Process.GameFrame;
import game.Process.GameMap;
import game.Process.GameState;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A very simple structure for the main game loop.
 */
public class GameLoop {
    // Private fields
    public static final int FPS = 25; // Bullet delay handler
    private boolean gameOver;
    private int numberOfPlayers;
    private final GameMap gameMap;
    private GameData gameData;
    private CopyOnWriteArrayList<User> users, finalList;
    private CopyOnWriteArrayList<MysteryBox> boxes;
    private static String[] boxTypes = {"boost", "health", "RPG", "shield"};
    private CopyOnWriteArrayList<Bullet> bullets;
    private ExecutorService executorService, clientsService, botService;

    /**
     * The constructor of the game loop.
     * To create the frame of the game.
     * @param gameData the data of the match
     * @param gameMap the map in the game
     * @param vector the players list
     */
    public GameLoop(GameMap gameMap, CopyOnWriteArrayList<User> vector, GameData gameData) {
        //
        this.gameMap = gameMap;
        users = (CopyOnWriteArrayList<User>) vector.clone();
        finalList = (CopyOnWriteArrayList<User>) vector.clone(); // We use this to tell everyone the game is over
        //
        this.numberOfPlayers = gameData.numberOfPeople;
        this.gameData = gameData;
        //
        initialize();
        invokeStart();
    }

    private void invokeStart() {
        for (User u : users)
            if (!u.isBot)
                u.write("start"); // We take the players out of waiting
    }

    private void initialize() {
        // Creating the states in here
        for (User u : users) {
            GameState state = new GameState(gameMap.locationController, gameData.tankSpeed);
            //
            state.speed = gameData.tankSpeed;
            state.health = gameData.tankHealth;
            state.setLimits(gameMap.getNumberOfRows(), gameMap.getNumberOfColumns());
            //
            u.setState(state);
        }
        gameMap.setPlaces(users);
    }

    /**
     * This must be called before the game loop starts.
     */
    public void init() {
        bullets = new CopyOnWriteArrayList<>();
        boxes = new CopyOnWriteArrayList<>();
        executorService = Executors.newCachedThreadPool();
        clientsService = Executors.newCachedThreadPool();
        botService = Executors.newCachedThreadPool();
    }

    /**
     * This method will create the bullet loop and services
     * for players and boxes.
     */
    public void runTheGame() {
        for (User u : users)
            if (u.isBot)
                botService.execute(new TankAI(u)); // The tank handler
            else
                clientsService.execute(new ClientHandler(u)); // Executing the clients
        clientsService.execute(new TankBullet());
        clientsService.execute(new MysteryMaker());
        while (gameCheck()) {
            long start = System.currentTimeMillis(); // Delay handling
            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                executorService.execute(bullet.getMover());
                if (!bullet.isAlive)
                    bullets.remove(bullet);
            }
            long delay = (1000 / FPS) - (System.currentTimeMillis() - start); // This is for handling the delays
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        gameOver = true;
        invokeAll();
        if (users.size() == 1)
            users.get(0).dataBox.win++;
        if (gameData.isTeamBattle)
            teamMatchResult();
        executorService.shutdownNow();
        clientsService.shutdownNow();
    }

    private void teamMatchResult() {
        int team = users.get(0).teamNumber;
        for (User u : finalList)
            if (u.teamNumber == team)
                if (!u.getUserName().equals(users.get(0).getUserName())) {
                    u.dataBox.win++;
                    u.dataBox.loose--;
                }
    }

    private boolean gameCheck() {
        if (gameData.isTeamBattle) {
            int teamOne = 0;
            int teamTwo = 0;
            for (User u : users)
                if (u.teamNumber == 1)
                    teamOne++;
                else
                    teamTwo++;
            return teamOne != 0 && teamTwo != 0;
        } else
            return numberOfPlayers > 1;
    }

    // This is for bullet and players handling
    private class TankBullet implements Runnable {
        @Override
        public void run() {
            while (!gameOver) {
                Iterator<User> userIterator = users.iterator();
                while (userIterator.hasNext()) {
                    User u = userIterator.next();
                    //
                    GameState state = u.getState();
                    //
                    for (Bullet b : bullets) {
                        //
                        if (!b.exploded) {
                            if (b.hitTheTank(state.locX, state.locY, state.width, state.height)) {
                                //
                                if (!b.isRPG) {
                                    b.counterDead = 0;
                                    b.exploded = true;
                                    b.makeSound = true;
                                }
                                if (!state.shield)
                                    state.health--;
                                if (state.health < 1) {
                                    state.health--;
                                    if (state.health < 1) {
                                        state.gameOver = true;
                                        //
                                        numberOfPlayers--;
                                        u.dataBox.loose++;
                                        users.remove(u);
                                        //
                                        u.updateDataBox();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private class MysteryMaker implements Runnable {
        Random random = new Random();
        long last = System.currentTimeMillis();
        @Override
        public void run() {
            while (!gameOver) {
                long now = System.currentTimeMillis();
                if (boxes.size() < 7 && now - last > 3000) {
                    while (true) {
                        int x = random.nextInt(gameMap.getNumberOfColumns());
                        int y = random.nextInt(gameMap.getNumberOfRows());
                        if (gameMap.binaryMap[y][x].getState() == 0) {
                            MysteryBox box = new MysteryBox();
                            box.type = boxTypes[random.nextInt(4)];
                            box.locX = x * GameMap.CHANGING_FACTOR + GameFrame.DRAWING_START_X + 20;
                            box.locY = y * GameMap.CHANGING_FACTOR + GameFrame.DRAWING_START_Y + 20;
                            boxes.add(box);
                            last = now;
                            break;
                        }
                    }
                }
            }
        }
    }

    private class TankAI implements Runnable {
        User user;
        TankAI(User user) { this.user = user; }
        @Override
        public void run() {
            user.getState().width = 25;
            user.getState().height = 25;
            while (!user.getState().gameOver) {
                update();
                // TODO 08-08-2020: add the update method
            }
        }

        private void update() {
            int rand = new Random().nextInt(32);
            user.getState().setToFalse();
            if (Integer.bitCount(rand & 1) == 1)
                user.getState().keyUP = true;
            if (Integer.bitCount(rand & 2) == 1)
                user.getState().keyDOWN = true;
            if (Integer.bitCount(rand & 4) == 1)
                user.getState().keyLEFT = true;
            if (Integer.bitCount(rand & 8) == 1)
                user.getState().keyRIGHT = true;
//            if (Integer.bitCount(rand & 16) == 1)
//                user.getState().shotFired = true;
            user.getState().update();
            user.updateDataBox();
//            if (user.getState().shotFired) {
//                Bullet bullet = new Bullet(user.getState().locX + user.getState().width / 2, user.getState().locY + user.getState().height / 2, gameMap, gameData.bulletSpeed, user.getBulletPath());
//                bullet.setDirections(user.getState().direction());
//                bullets.add(bullet);
//                if (user.getState().shooter) {
//                    bullet.isRPG = true;
//                    bullet.lifeTime = 8;
//                }
//            }
            for (MysteryBox box : boxes) {
                if (box.gotTheBox(user.getState().locX, user.getState().locY, user.getState().width, user.getState().height)) {
                    if (user.getState().takeBox(box.type)) {
                        boxes.remove(box);
                        break;
                    }
                }
            }
            try {
                user.out.reset(); // This is for sending the new state, it helps the syncing between client and server
            } catch (IOException e) {
                e.printStackTrace();
            }
            // giving the data
            write(bullets, user);
            write(boxes, user);
            write(users, user);
            write(gameMap, user); // I get some image exceptions in here
            user.getState().inUse = false; // Free the state
        }
    }

    // This method will tell the sockets to close
    private void invokeAll() {
        for (User u : finalList)
            while (true)
                if (!u.getState().inUse && !u.isBot) {
                    write(-1, u); // Means the game is over now
                    if (gameData.isTeamBattle)
                        if (users.get(0).teamNumber == 1)
                            write("Blue team", u);
                        else
                            write("Red team", u);
                    else
                        write(users.get(0).getUserName(), u);
                    break;
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
                state.inUse = true; // This boolean is used for locking this user state to avoid sending other things mean while this loop is executing
                write(1, u); // This is for letting the client side know if we are sending the data. 1 means we are sending and -1 means not (invokeAll)
                // Getting the updated data
                state.keyUP = (boolean) u.read();
                state.keyDOWN = (boolean) u.read();
                state.keyLEFT = (boolean) u.read();
                state.keyRIGHT = (boolean) u.read();
                state.mousePress = (boolean) u.read();
                state.mouseX = (int) u.read();
                state.mouseY = (int) u.read();
                state.shotFired = (boolean) u.read();
                if (!state.gameOver) {
                    // Updating while the player is on the game
                    state.update();
                    u.updateDataBox();
                    if (state.shotFired) {
                        Bullet bullet = new Bullet(state.locX + state.width / 2, state.locY + state.height / 2, gameMap, gameData.bulletSpeed, u.getBulletPath());
                        bullet.setDirections(state.direction());
                        bullets.add(bullet);
                        if (state.shooter) {
                            bullet.isRPG = true;
                            bullet.lifeTime = 8;
                        }
                    }
                    u.setState(state);
                    for (MysteryBox box : boxes) {
                        if (box.gotTheBox(state.locX, state.locY, state.width, state.height)) {
                            if (state.takeBox(box.type)) {
                                boxes.remove(box);
                                break;
                            }
                        }
                    }
                }
                try {
                    u.out.reset(); // This is for sending the new state, it helps the syncing between client and server
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // giving the data
                write(bullets, u);
                write(boxes, u);
                write(users, u);
                write(gameMap, u); // I get some image exceptions in here
                state.inUse = false; // Free the state
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
