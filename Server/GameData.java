package game.Server;

import java.io.Serializable;

/**
 * This class is a single game data which will
 * save the game information.
 */
public class GameData implements Serializable {
    public int bulletSpeed, tankSpeed, wallHealth, tankHealth;
    public int numberOfPeople, port, numberOfRounds, playersOnline;
    public String matchType, ip;

    @Override
    public String toString() {
        return "Capacity : " + numberOfPeople + "  -  Match type : " + matchType + " - Players in : " + playersOnline + " || IP : " + ip + "  -  PORT : " + port;
    }

}
