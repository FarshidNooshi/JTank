package game.Begin;

import java.util.Random;

public class GameMap {

    private int randomSeed = 1111;
    Random random = new Random(randomSeed);
    static final int CHANGING_FACTOR = 80;

    int numberOfRows;
    int numberOfColumns;
    int[][] binaryMap;

    public GameMap() {
        numberOfRows = random.nextInt(12) + 7;
        numberOfColumns = random.nextInt(12) + 7;
    }

    public void init() {
        binaryMap = new int[numberOfRows][numberOfColumns];
    }
}
