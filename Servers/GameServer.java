package game.Servers;

import game.Control.LocationController;
import game.Process.Bullet;
import game.Process.GameMap;
import game.Process.GameState;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {

    private static int counter = 100;

    public static void main (String[] args) {

        ExecutorService service = Executors.newCachedThreadPool();
        CopyOnWriteArrayList<Bullet> bullets = new CopyOnWriteArrayList<>();

        GameMap gameMap = new GameMap();
        gameMap.init();

        try (ServerSocket welcomeSocket = new ServerSocket(2020)) {

            Socket socket = welcomeSocket.accept();

            service.execute(new InGameClient(socket, gameMap, bullets));

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

        public InGameClient (Socket socket, GameMap gameMap, CopyOnWriteArrayList<Bullet> bullets) {
            this.socket = socket;
            this.gameMap = gameMap;
            this.bullets = bullets;
            executorService = Executors.newCachedThreadPool();
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

                while (true) {

                    GameState state = (GameState) ois.readObject();

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

                    oos.writeObject(state);
                    oos.reset();
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
