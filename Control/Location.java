package game.Control;

import game.Process.GameMap;

/**
 * This class creates for keeping the data of the
 * game walls.
 * Including the starting coordinates and the ending
 * coordinates to avoid calculations in the game processing.
 *
 */
public class Location {

    // The two points of the walls
    private int topX;
    private int topY;
    private int bottomX;
    private int bottomY;

    /**
     * The constructor of the Location class.
     *
     * @param topX the first point x coordinate
     * @param topY the first point y coordinate
     */
    public Location (int topX, int topY) {
        this.topX = topX;
        this.topY = topY;
        bottomX = topX + GameMap.CHANGING_FACTOR; // We calculate the second point coordinates base on the
        bottomY = topY + GameMap.CHANGING_FACTOR; // changing factor in the game
    }

    /**
     * This method checks if the user tank and the wall are
     * overlapping each others or not.
     *
     * @param userX the tank x coordinate
     * @param userY the tank y coordinate
     * @return the result of overlapping
     */
    public boolean isOverlap (int userX, int userY) {
        return ( topX < userX && bottomX > userX || topX < userX + GameMap.CHANGING_FACTOR / 2 && bottomX > userX + GameMap.CHANGING_FACTOR / 2 ) &&
                ( topY < userY && bottomY > userY || topY < userY + GameMap.CHANGING_FACTOR / 2 && bottomY > userY + GameMap.CHANGING_FACTOR / 2 );
    }

    /**
     * This method will check if the given coordinates
     * are the coordinates of this wall or not.
     *
     * @param inputX the input x coordinate
     * @param inputY the input y coordinate
     * @return match the wall or not
     */
    public boolean isMatch (int inputX, int inputY) {
        return inputX == topX && inputY == topY;
    }
}
