package game.Servers;

import game.Control.LocationController;
import game.Process.Bullet;
import game.Process.GameMap;
import game.Process.GameState;
import game.Process.Main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {

    private static int counter = 100;
    private static HashMap<Integer, GameState> states;

    public static void main (String[] args) {

        ExecutorService service = Executors.newCachedThreadPool();
        CopyOnWriteArrayList<Bullet> bullets = new CopyOnWriteArrayList<>();
        states = new HashMap<>();

        GameMap gameMap = new GameMap();
        gameMap.init();

        try (ServerSocket welcomeSocket = new ServerSocket(2020)) {

            for (int i = 0; i < 100; i++) {
                Socket socket = welcomeSocket.accept();

                service.execute(new InGameClient(socket, gameMap, bullets, counter));
                counter++;
            }

        } catch (IOException e) {
            System.out.println("Error in starting server :: " + e.getMessage());
        } finally {
            service.shutdown();
        }
    }

    private static class InGameClient implements Runnable {

        private Socket socket;
        private GameMap gameMap;
        private ExecutorService executorService;
        private CopyOnWriteArrayList<Bullet> bullets;
        private final int ID;

        public InGameClient (Socket socket, GameMap gameMap, CopyOnWriteArrayList<Bullet> bullets, int ID) {
            this.socket = socket;
            this.gameMap = gameMap;
            this.bullets = bullets;
            executorService = Executors.newCachedThreadPool();
            this.ID = ID;
        }

        @Override
        public void run() {
            System.out.println("Client connected :: successfully");
            try (InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream();
            ) {
                ObjectOutputStream oos = new ObjectOutputStream(outputStream);
                ObjectInputStream ois = new ObjectInputStream(inputStream);

                oos.writeObject(gameMap);
                outputStream.write(ID);

                while (true) {

                    GameState state = (GameState) ois.readObject();

                    if (states.containsKey(ID)) {
                        states.replace(ID, state);
                    } else
                        states.put(ID, state);


                    if (state.shotFired) {
                        int direction = state.direction();
                        Bullet bullet = new Bullet(state.locX, state.locY, gameMap);
                        bullets.add(bullet);
                        bullet.setDirections(direction);
                    }
                    // The bullets loop
                    Iterator<Bullet> iterator = bullets.iterator();
                    while (iterator.hasNext()) {
                        Bullet bullet = iterator.next();
                        if (LocationController.tankGotShot(bullet.locX + bullet.diam / 2, bullet.locY + bullet.diam / 2, state.locX, state.locY) && !bullet.justShot) {
                            state.gameOver = true;
                            bullet.isAlive = false;
                        }
                        if (bullet.isAlive)
                            executorService.execute(bullet.getMover());
                        else
                            bullets.remove(bullet);
                    }

                    state.update();

                    for (Map.Entry<Integer, GameState> entry : states.entrySet()) {
                        outputStream.write(1);
                        outputStream.write(entry.getKey());
                        oos.reset();
                        oos.writeObject(entry.getValue());
                    }
                    outputStream.write(0);
                    oos.writeObject(gameMap.binaryMap);
                    if (bullets.size() == 0)
                        outputStream.write(0);
                    else {
                        for (Bullet bullet : bullets) {
                            outputStream.write(1);
                            System.out.println(bullet.locX + "  " + bullet.locY);
                            oos.reset();
                            oos.writeObject(bullet);
                        }
                        outputStream.write(0);
                    }
                    oos.flush();
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error in connecting :: " + e.getMessage());
            }
        }
    }
}
