package game.Server;

import java.io.Serializable;

/**
 * This class is a single game data which will
 * save the game information.
 */
public class GameData implements Serializable {
    int numberOfPeople, port;
    String matchType, ip;
    @Override
    public String toString() {
        return  "Capacity = " + numberOfPeople +
                ", Match type = " + matchType +
                ", IP ='" + ip + '\'' +
                ", port =" + port;
    }
}
