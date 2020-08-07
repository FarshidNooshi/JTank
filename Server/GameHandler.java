package game.Server;

import game.Control.LocationController;
import game.Process.GameMap;
import game.Process.ThreadPool;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class creates a game and will
 * get the users.
 */
public class GameHandler implements Runnable {
    // Fields
    private CopyOnWriteArrayList<User> playersVector;
    private GameData data;
    private int rounds, numberOfPlayers;

    /**
     * The main constructor of the class.
     * @param vector the list of the players
     * @param data the gama setting
     * @param numberOfPlayers the number of the players
     */
    public GameHandler(CopyOnWriteArrayList<User> vector, GameData data, int numberOfPlayers) {
        //
        this.playersVector = vector;
        this.data = data;
        //
        this.numberOfPlayers = numberOfPlayers;
        rounds = data.numberOfRounds;
    }

    @Override
    public void run() {
        init();
        while (rounds > 0) {
            GameMap gameMap = new GameMap(new LocationController(), data);
            GameLoop gameLoop = new GameLoop(gameMap, playersVector, data);
            gameLoop.init();
            gameLoop.runTheGame();
            updateRatings();
            rounds--;
        }
    }

    private void init() {
        int join = 0;
        try (ServerSocket serverSocket = new ServerSocket(data.port)) {
            for (int i = 0; i < numberOfPlayers; i++) {
                Socket socket = serverSocket.accept();
                join++;
                data.playersOnline = join;
                String userName = new Scanner(socket.getInputStream()).nextLine();
                for (int j = 0; j < join; j++)
                        if (playersVector.get(j).getUserName().equals(userName)) {
                            playersVector.get(j).setClientSocket(socket);
                            playersVector.get(j).out = new ObjectOutputStream(playersVector.get(j).getClientSocket().getOutputStream());
                            playersVector.get(j).in = new ObjectInputStream(playersVector.get(j).getClientSocket().getInputStream());// The client hand side sets in User init
                            break;
                        }
            }
            removeGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeGame() {
        Main.data.remove(data);
    }

    private void updateRatings() {
        for (User u : playersVector) {
            u.isBestPlayer = false;
            u.isWorstPlayer = false;
        }
        User best = playersVector.get(0), worst = playersVector.get(0);
        for (int i = 1; i < numberOfPlayers; i++) {
            User temp = playersVector.get(i);
            if (temp.dataBox.win >= best.dataBox.win)
                best = temp;
            if (temp.dataBox.loose >= worst.dataBox.loose)
                worst = temp;
        }
        best.isBestPlayer = true;
        worst.isWorstPlayer = true;
    }
}
