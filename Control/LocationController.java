package game.Control;

import game.Process.GameMap;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class known as location controller is
 * the controller to check if the tanks and the
 * walls are overlapping each other or not.
 * Basically it keeps all the wall date and has
 * methods to check overlapping.
 */
public class LocationController {
    // The list of the walls ,each type
    private CopyOnWriteArrayList<Location> locations;
    private boolean created = false;

    /**
     * The init method will create the objects
     * required.
     */
    public void init() {
        locations = new CopyOnWriteArrayList<>();
        created = true;
    }

    /**
     * A method for adding a new wall location.
     *
     * @param location the new location to add
     */
    public void add(Location location) {
        if (created)
            locations.add(location);
        else
            init();
    }

    /**
     * This method will remove the wall that matches to
     * the locations we give to it.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void remove(int x, int y) {
        // Check for class creations
        if (!created)
            init();
        // Search for any match
        for (Location l : locations)
            if (l.isMatch(x, y)) {
                locations.remove(l);
                break;
            }
    }

    /**
     * this method removes a specific wall(which is in a same place as point(x,y)) from the list
     *
     * @param x is the x coordinate of the wall
     * @param y is the y coordinate of the wall
     */
    public void removeBinary(int x, int y) {
        // Check for class creations
        if (!created)
            init();
        // Search for any match
        for (Location l : locations)
            if (l.samePlace(x, y)) {
                locations.remove(l);
                break;
            }
    }

    /**
     * This method iterates through the walls
     * and will check the overlapping in tank
     * with each wall.
     *
     * @param x      the tank x coordinate
     * @param y      the tank y coordinate
     * @param width  the width of the shape
     * @param height the height of the shape
     * @return making overlap or not
     */
    public boolean check(int x, int y, int width, int height) {
        // Check for class creations
        if (!created)
            return true;
        // Search for any overlap
        for (Location l : locations)
            if (l.isOverlap(x, y, 2, width, height))
                return false;
        return true;
    }

    /**
     * This method will check if a bullet has overlapped
     * into a wall.
     * It will return the location of thw wall.
     *
     * @param x the bullet x coordinate
     * @param y the bullet y coordinate
     * @return the location of the wall
     */
    public Location bulletWallCheck(int x, int y) {
        if (!created)
            return null;
        for (Location l : locations) {
            if (l.isOverlap(x, y, 0, 2, 2)) {
                if (l.type == 1) {
                    l.health--;
                    if (l.health < 0)
                        locations.remove(l);
                }
                return l;
            }
        }
        return null;
    }

    /**
     * This method will check if the tank got shot
     * by any bullet or not.
     *
     * @param bullX the bullet x coordinate
     * @param bullY the bullet y coordinate
     * @param tankX the tank x coordinate
     * @param tankY the tank y coordinate
     * @return true or false
     */
    public boolean tankGotShot(int bullX, int bullY, int tankX, int tankY, int width, int height) {
        return tankX < bullX && bullX < tankX + width && tankY < bullY && bullY < tankY + height;
    }
}
