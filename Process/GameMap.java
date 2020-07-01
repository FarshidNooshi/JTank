package game.Process;

import game.Control.Location;
import game.Control.LocationController;

import java.util.Random;

public class GameMap {

    private int randomSeed = 1111;
    Random random = new Random(randomSeed);
    public static final int CHANGING_FACTOR = 80;

    int numberOfRows;
    int numberOfColumns;
    int[][] binaryMap;

    public GameMap() {
        numberOfRows = random.nextInt(4) + 4;
        numberOfColumns = random.nextInt(12) + 4;
    }

    public void init() {
        binaryMap = new int[numberOfRows][numberOfColumns];
        LocationController.init();
        makeGameMap();
    }

    private void makeGameMap() {
        for (int y = 0; y < numberOfRows; y++) {
            for (int x = 0; x < numberOfColumns; x++) {
                binaryMap[y][x] = random.nextInt(3);
                if (binaryMap[y][x] != 0)
                    LocationController.add(new Location(GameFrame.DRAWING_START_X + x * GameMap.CHANGING_FACTOR, GameFrame.DRAWING_START_Y + y * GameMap.CHANGING_FACTOR));
            }
        }
        setPlaces();
    }

    private void setPlaces () {
        while (true) {
            int x = random.nextInt(numberOfColumns);
            int y = random.nextInt(numberOfRows);
            if (binaryMap[y][x] == 0) {
                GameState.setLocation(x * GameMap.CHANGING_FACTOR + GameFrame.DRAWING_START_X,y * GameMap.CHANGING_FACTOR + GameFrame.DRAWING_START_Y);
                break;
            }
        }
    }
}
