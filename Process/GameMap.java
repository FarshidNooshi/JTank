package game.Process;

import game.Control.Location;
import game.Control.LocationController;
import game.Server.GameData;
import game.Server.User;

import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

/**
 * This class is our map of the game.
 * It creates the map places the walls and
 * places the tanks in the empty spaces.
 */
public class GameMap implements Serializable {

    public static final int CHANGING_FACTOR = 80; // This is the factor that we show the map bigger size in gui
    Cell[][] binaryMap; // The array of the map
    public boolean gameOver = false;
    // The data of the map
    private int numberOfRows;
    private int numberOfColumns;
    private Random random = new Random(); // The random instance
    public transient LocationController locationController;
    private GameData gameData;

    /**
     * The constructor of the map class.
     */
    public GameMap(LocationController locationController, GameData gameData) {
        numberOfRows = random.nextInt(4) + 4;
        numberOfColumns = random.nextInt(10) + 4;
        this.locationController = locationController;
        this.gameData = gameData;
    }

    /**
     * This method will create the map game.
     * Also creates the location controller.
     */
    public void init() {
        binaryMap = new Cell[numberOfRows][numberOfColumns];
        locationController.init(); // Creating the controller
        makeGameMap();
    }

    /**
     * This method will iterate in the array
     * and will make the map binary form to show it
     * in a big size in gui.
     */
    private void makeGameMap() {
        for (int y = 0; y < numberOfRows; y++) {
            for (int x = 0; x < numberOfColumns; x++) {
                binaryMap[y][x] = new Cell(random.nextInt(3), random.nextInt(2), gameData.wallHealth);
                if (random.nextInt(100) % 2 == 0)
                    binaryMap[y][x].setState(0);
                if (binaryMap[y][x].getState() != 0)
                    locationController.add(new Location(x, y, game.Process.GameFrame.DRAWING_START_X + x * GameMap.CHANGING_FACTOR, game.Process.GameFrame.DRAWING_START_Y + y * GameMap.CHANGING_FACTOR, binaryMap[y][x].getState(), gameData.wallHealth));
            }
        }
    }

    /**
     * This method will place the tanks into empty spaces.
     */
    public void setPlaces(Vector<User> users) {
        for (User u : users) {
            while (true) {
                int x = random.nextInt(numberOfColumns); // A random place for the states
                int y = random.nextInt(numberOfRows);

                if (binaryMap[y][x].getState() == 0) {
                    u.getState().setLocation(x * GameMap.CHANGING_FACTOR + game.Process.GameFrame.DRAWING_START_X, y * GameMap.CHANGING_FACTOR + game.Process.GameFrame.DRAWING_START_Y);
                    binaryMap[y][x].setState(-1); // Showing the tank is in this house
                    break;
                }
            }
        }
        flushTank(); // We need to empty the tank places
    }

    /**
     * This method will clear the tank places
     * in the map so when we want to draw the map
     * we don't get any problems.
     */
    private void flushTank() {
        for (int y = 0; y < numberOfRows; y++)
            for (int x = 0; x < numberOfColumns; x++)
                if (binaryMap[y][x].getState() == -1)
                    binaryMap[y][x].setState(0);
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    //TODO: add a method to create a map that all the tanks are connected to each other.
}
