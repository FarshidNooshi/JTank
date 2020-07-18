package game.Process;

import game.Control.Location;
import game.Control.LocationController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Bullet implements Serializable {

    transient private GameMap gameMap; //Each bullet needs the map
    //Location fields
    public int locX, locY, diam;
    transient private int firstX, firstY;
    transient public boolean isAlive, justShot; //Status fields
    //Speed and time fields
    transient public static int speed = 8;
    transient private long start;
    transient private int mapRowsLimit, mapColsLimit; //The limits fields
    transient private boolean UP, DOWN, RIGHT, LEFT; //Movement booleans

    /**
     * The constructor of the bullet class.
     *
     * @param locX the first x coordinate
     * @param locY the first y coordinate
     * @param gameMap the game map instance
     */
    public Bullet (int locX, int locY ,GameMap gameMap) {
        // The starting point of the square
        this.locX = locX + GameMap.CHANGING_FACTOR / 4; // This is for putting the bullet at the
        this.locY = locY + GameMap.CHANGING_FACTOR / 4; // center of the tank
        // Keeping the old coordinate
        firstX = this.locX;
        firstY = this.locY;
        // Setting the limits
        this.mapRowsLimit = gameMap.numberOfRows;
        this.mapColsLimit = gameMap.numberOfColumns;
        // The radius of the circle
        diam = 8;
        isAlive = true;
        justShot = true;
        this.gameMap = gameMap;
        start = System.currentTimeMillis(); // Keeping the start time
    }

    /**
     * This method will get the tank location and will
     * chose the bullet direction to go.
     *
     * @param directions the tank direction
     */
    public void setDirections (int directions) {
        switch (directions)
        {
            case 225:
                UP = true;
                LEFT = true;
                break;
            case 315:
                UP = true;
                RIGHT = true;
                break;
            case 45:
                DOWN = true;
                RIGHT = true;
                break;
            case 135:
                DOWN = true;
                LEFT = true;
                break;
            case 270:
                UP = true;
                break;
            case 90:
                DOWN = true;
                break;
            case 180:
                LEFT = true;
                break;
            case 0:
                RIGHT = true;
                break;
        }
    }

    /**
     * This method will create a runnable of the BulletMove
     * and will give it back so it can be execute.
     *
     * @return an instance of the runnable
     */
    public BulletMove getMover () {
        return new BulletMove();
    }

    /*

        This class is an inner class which will
        update the bullet class.
        It changes the bullet place and will check
        the walls and the changes in that the bullet had
        make in the map.

     */
    class BulletMove implements Runnable, Serializable {

        /*
            This method will change the directions of the bullet
            base on the wall that it hits.
         */
        private void wallChangingWay (Location location) {
            // Getting the locations needed of the wall
            int centerX = locX + diam / 2; // We locate the center of
            int centerY = locY + diam / 2; // bullet
            // And then check for points of the circle for overlapping
            boolean top = location.isOverlap(centerX, centerY - diam / 2, 0);
            boolean bottom = location.isOverlap(centerX, centerY + diam / 2, 0);
            boolean left = location.isOverlap(centerX - diam / 2, centerY, 0);
            boolean right = location.isOverlap(centerX + diam / 2, centerY, 0);

            if (top && !bottom) {
                UP = false;
                DOWN = true;
            }
            if (bottom && !top) {
                UP = true;
                DOWN = false;
            }
            if (left && !right) {
                LEFT = false;
                RIGHT = true;
            }
            if (right && !left) {
                LEFT = true;
                RIGHT = false;
            }
        }

        /*
            This method will update the movement of the
            bullet.
            Changing the place and the borders bouncy.
         */
        private void update () {
            // Update the location
            if (UP)
                locY -= speed;
            if (DOWN)
                locY += speed;
            if (LEFT)
                locX -= speed;
            if (RIGHT)
                locX += speed;

            // The walls bouncy
            if (locX + diam / 2 <= GameFrame.DRAWING_START_X) {
                LEFT = false;
                RIGHT = true;
            }
            if (locX  + diam / 2 >= mapColsLimit * GameMap.CHANGING_FACTOR + GameFrame.DRAWING_START_X) {
                RIGHT = false;
                LEFT = true;
            }
            if (locY  + diam / 2 <= GameFrame.DRAWING_START_Y) {
                UP = false;
                DOWN = true;
            }
            if (locY + diam / 2 >= mapRowsLimit * GameMap.CHANGING_FACTOR + GameFrame.DRAWING_START_Y) {
                UP = true;
                DOWN = false;
            }

            locX = Math.max(locX, GameFrame.DRAWING_START_X); // Setting the new locations based on the limits
            locX = Math.min(locX, mapColsLimit * GameMap.CHANGING_FACTOR - GameMap.CHANGING_FACTOR / 16 + GameFrame.DRAWING_START_X);
            locY = Math.max(locY, GameFrame.DRAWING_START_Y);
            locY = Math.min(locY, mapRowsLimit * GameMap.CHANGING_FACTOR - GameMap.CHANGING_FACTOR / 16 + GameFrame.DRAWING_START_Y);
        }

        @Override
        public void run() {
            // The time checking
            int time = (int) ((System.currentTimeMillis() - start) / 1000);
            if (time >= 4)
                isAlive = false; // The time limit

            if (justShot)
            {
                if (Math.abs(firstX - locX) > GameMap.CHANGING_FACTOR / 5 || Math.abs(firstY - locY) > GameMap.CHANGING_FACTOR / 5)
                    justShot = false; // This is for avoiding destroying the tank as soon as the bullet fired
            }

            // To check if the bullet is hitting any walls
            Location location = LocationController.bulletWallCheck(locX + diam / 2, locY + diam / 2);

            if (location != null)
            {
                if (location.type == 1)
                {
                    isAlive = false; // This means that the bullet has hit a breakable wall
                    gameMap.binaryMap[location.getBinaryY()][location.getBinaryX()] = 0;
                    return;
                } else {
                    wallChangingWay(location);
                }
            }
            // The bullet update method
            update();
        }
    }
}
