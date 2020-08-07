package game.Server;

import java.io.Serializable;

/**
 * Data box will send the data we need.
 * Uses less storage.
 */
public class DataBox implements Serializable {
    public String userName;
    public int locX, locY, direction, width, height, win, loose, health;
    public boolean gameOver;
}
