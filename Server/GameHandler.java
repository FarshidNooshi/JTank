package game.Server;

import game.Process.GameMap;
import game.Process.ThreadPool;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class GameHandler implements Runnable {

    private Vector<User> playersVector;


    public GameHandler(Vector<User> vector) {
        this.playersVector = vector;
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
        try (ServerSocket serverSocket = new ServerSocket(2726)) {
            for (int i = 0; i < playersVector.size(); i++) {
                Socket socket = serverSocket.accept();
                String userName = new Scanner(socket.getInputStream()).nextLine();
                System.out.println("WTF");
                for (int j = 0; j < playersVector.size(); j++)
                    if (playersVector.get(i).getUserName().equals(userName)) {
                        playersVector.get(i).setClientSocket(socket);
                        break;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



/*
    public static void startTheGame(int numberOfPlayers) {

        GameFrame frame = new GameFrame("JTank");
        frame.setLocationRelativeTo(null); // put frame at center of screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.initBufferStrategy();
        GameMap gameMap = new GameMap();
        gameMap.init();
        frame.setGameMap(gameMap);
        // Create and execute the game-loop
        GameLoop game = new GameLoop(frame, numberOfPlayers);
        game.init();
        ThreadPool.execute(game);
        // and the game starts ...
    }
*/
