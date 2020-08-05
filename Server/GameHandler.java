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

public class GameHandler implements Runnable {

    private Vector<User> playersVector;
    private int numberOfPlayers;
    private GameData data;
    private int rounds;

    public GameHandler(Vector<User> vector, GameData data, int numberOfPlayers) {
        this.playersVector = vector;
        this.numberOfPlayers = numberOfPlayers;
        this.data = data;
        rounds = data.numberOfRounds;
    }

    @Override
    public void run() {
        init();
        while (rounds > 0) {
            System.out.println("Round start :: " + rounds);
            GameMap gameMap = new GameMap(new LocationController(), data);
            gameMap.init();
            GameLoop gameLoop = new GameLoop(gameMap, playersVector, data);
            gameLoop.init();
            try {
                gameLoop.runTheGame();
            } catch (IOException ignored){
            }
            rounds--;
        }
    }

    private void init() {
        int join = 0;
        try (ServerSocket serverSocket = new ServerSocket(data.port)) {
            for (int i = 0; i < numberOfPlayers; i++) {
                Socket socket = serverSocket.accept();
                join++;
                //
                String userName = new Scanner(socket.getInputStream()).nextLine();
                //
                for (int j = 0; j < join; j++)
                        if (playersVector.get(j).getUserName().equals(userName)) {
                            playersVector.get(j).setClientSocket(socket);
                            //
                            playersVector.get(j).out = new ObjectOutputStream(playersVector.get(j).getClientSocket().getOutputStream());
                            playersVector.get(j).in = new ObjectInputStream(playersVector.get(j).getClientSocket().getInputStream());// The client hand side sets in User init
                            //
                            break;
                        }
            }
            //
            removeGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method will remove the current game from the list of data games
    private void removeGame() {
        Main.data.remove(data);
    }
}
