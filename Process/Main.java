package game.Process;

import game.Log;

import javax.swing.*;
import java.awt.*;

/**
 * Program start.
 * This is the client starting part where we open the
 * login page and get user login or sig up inputs
 * to enter the user into the game server.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        // Initialize the global thread-pool
        ThreadPool.init();
        //temporary for writing the program.
        Main.startTheGame();
        // After the player clicks 'PLAY' ...
//        EventQueue.invokeLater(Log::run);
    }

    /**
     * This is the part where we connect the user to the
     * server game that the user chooses or creates.
     */
    public static void startTheGame() throws InterruptedException {

        GameFrame frame = new GameFrame("JTank");
        frame.setLocationRelativeTo(null); // put frame at center of screen
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
        frame.setVisible(true);
        frame.initBufferStrategy();
        GameMap gameMap = new GameMap();
        gameMap.init();
        frame.setGameMap(gameMap);
        // Create and execute the game-loop
        GameLoop game = new GameLoop(frame);
        game.init();
        ThreadPool.execute(game);
        // and the game starts ...
    }
}
