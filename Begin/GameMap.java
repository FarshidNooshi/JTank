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
        numberOfRows = random.nextInt(4) + 4;
        numberOfColumns = random.nextInt(12) + 4;
    }

    public void init() {
        binaryMap = new int[numberOfRows][numberOfColumns];
        makeGameMap();
    }

    private void makeGameMap() {
        for (int y = 0; y < numberOfRows; y++)
            for (int x = 0; x < numberOfColumns; x++)
               binaryMap[y][x] = random.nextInt() % 3;
    }
}
