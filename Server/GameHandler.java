package game.Server;

import game.Process.GameMap;
import game.Process.ThreadPool;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class GameHandler implements Runnable {

    private Vector<User> playersVector;
    private GameData data;

    public GameHandler(Vector<User> vector, GameData data) {
        this.playersVector = vector;
        this.data = data;
    }

    @Override
    public void run() {
        init();
        GameMap gameMap = new GameMap();
        gameMap.init();
        GameLoop gameLoop = new GameLoop(gameMap, playersVector);
        gameLoop.init();
        ThreadPool.execute(gameLoop);
    }

    private void init() {
        try (ServerSocket serverSocket = new ServerSocket(data.port)) {
            for (int i = 0; i < playersVector.size(); i++) {
                Socket socket = serverSocket.accept();
                String userName = new Scanner(socket.getInputStream()).nextLine();
                //TODO: 03-08-2020 need to hold the players wait for everyone to join
                for (int j = 0; j < playersVector.size(); j++)
                    if (playersVector.get(i).getUserName().equals(userName)) {
                        playersVector.get(i).setClientSocket(socket); // The client hand side sets in User init
                        break;
                    }
            }
            //
            removeGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeGame() {
        Main.data.remove(data);
    }
}
