package game.Process;

import game.Control.Location;
import game.Control.LocationController;
import java.util.Random;

/**
 * This class is our map of the game.
 * It creates the map places the walls and
 * places the tanks in the empty spaces.
 *
 */
public class GameMap {

    private Random random = new Random(); // The random instance

    public static final int CHANGING_FACTOR = 80; // This is the factor that we show the map bigger size in gui

     // The data of the map
    int numberOfRows;
    int numberOfColumns;
    int[][] binaryMap; // The array of the map

    /**
     * The constructor of the map class.
     *
     */
    public GameMap() {
        numberOfRows = random.nextInt(4) + 4;
        numberOfColumns = random.nextInt(12) + 4;
    }

    /**
     * This method will create the map game.
     * Also creates the location controller.
     *
     */
    public void init() {
        binaryMap = new int[numberOfRows][numberOfColumns];
        LocationController.init(); // Creating the controller
        makeGameMap();
    }

    /*
     This method will iterate in the array
     and will make the map binary form to show it
     in a big size in gui.
     */
    private void makeGameMap() {
        for (int y = 0; y < numberOfRows; y++)
        {
            for (int x = 0; x < numberOfColumns; x++)
            {
                binaryMap[y][x] = random.nextInt(3);
                if (random.nextInt(100) % 5 == 0)
                    binaryMap[y][x] = 2;
                if (binaryMap[y][x] != 0)
                    LocationController.add(new Location(x, y,GameFrame.DRAWING_START_X + x * GameMap.CHANGING_FACTOR, GameFrame.DRAWING_START_Y + y * GameMap.CHANGING_FACTOR));
            }
        }
    }

    /**
     * This method will place the tanks into empty spaces.
     *
     * @param gameState the game state or the tank
     */
    public void setPlaces (GameState gameState) {
        while (true)
        {
            int x = random.nextInt(numberOfColumns); // A random place for the states
            int y = random.nextInt(numberOfRows);

            if (binaryMap[y][x] == 0)
            {
                gameState.setLocation(x * GameMap.CHANGING_FACTOR + GameFrame.DRAWING_START_X,y * GameMap.CHANGING_FACTOR + GameFrame.DRAWING_START_Y);
                binaryMap[y][x] = -1; // Showing the tank is in this house
                break;
            }
        }
        flushTank(); // We need to empty the tank places
    }

    /*
    This method will clear the tank places
    in the map so when we want to draw the map
    we don't get any problems.
     */
    private void flushTank () {
        for (int y = 0; y < numberOfRows; y++)
            for (int x = 0; x < numberOfColumns; x++)
                if (binaryMap[y][x] == -1)
                    binaryMap[y][x] = 0;
    }
}
