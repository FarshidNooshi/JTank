package game.Server;

import java.io.Serializable;

public class MysteryBox implements Serializable {
    public String type;
    public int locX, locY;
    public boolean gotTheBox (int x, int y, int width, int height) { return x < locX && locX < x + width && y < locY && locY < y + height; }
}
