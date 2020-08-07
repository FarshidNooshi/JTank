package game.Process;

import game.Log;

import java.awt.*;

/**
 * Program start.
 * This is the client starting part where we open the
 * login page and get user login or sig up inputs
 * to enter the user into the game server.
 */
public class Main {// TODO: 07-Aug-20 add time and stuff like that to the game.
    public static void main(String[] args) {
        // Initialize the global thread-pool
        ThreadPool.init();
        // Start from login page
        EventQueue.invokeLater(new Log()::run);
    }
}
