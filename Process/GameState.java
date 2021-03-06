package game.Process;

import game.Control.LocationController;

import java.awt.event.*;
import java.io.Serializable;

/**
 * This class holds the state of game and all of its elements.
 * This class also handles user inputs, which affect the game state.
 */
public class GameState implements Serializable {
    // Private fields
    public int locX, locY, width, height, speed, health;
    public boolean gameOver, shotFired, inUse, shield;
    private transient int mapRowsLimit, mapColsLimit, currentDirection; // This is the map limits
    public transient boolean keyUP, keyDOWN, keyRIGHT, keyLEFT, mousePress; // true if the appropriate arrow key is pressed.
    public transient int mouseX, mouseY; // the positions of the mouse clicked pos.
    private transient long shotTimeLimit, pickTime;// just for remembering the timeLimit of the shots. ;)
    private transient VectorFactory vectorFactory; // Each state has its own vector factory
    private transient LocationController locationController;
    public transient boolean booster, shooter;

    /**
     * The game state constructor.
     * @param speed the tank first speed
     * @param locationController the instance of location controller
     */
    public GameState(LocationController locationController, int speed) {
        gameOver = false;
        shotFired = false;
        this.speed = speed;
        currentDirection = 0;
        shotTimeLimit = 0;
        pickTime = 0;
        //
        keyUP = false;
        keyDOWN = false;
        keyRIGHT = false;
        keyLEFT = false;
        //
        mousePress = false;
        mouseX = 0;
        mouseY = 0;
        //
        booster = false;
        shooter = false;
        //
        vectorFactory = new VectorFactory(speed);
        this.locationController = locationController;
    }

    /**
     * A setter method for setting the locations.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setLocation(int x, int y) {
        locX = x;
        locY = y;
    }

    /**
     * This is a method for setting the borders
     * limits.
     *
     * @param mapRows the rows limit
     * @param mapCols the cols limit
     */
    public void setLimits(int mapRows, int mapCols) {
        mapRowsLimit = mapRows;
        mapColsLimit = mapCols;
    }

    /**
     * The method which updates the game state.
     */
    public void update() {
        // Mystery box check
        long now = System.currentTimeMillis();
        if (now - pickTime > 2000) {
            if (now - pickTime > 4000) {
                shield = false;
            }
            if (booster) {
                speed /= 2;
                vectorFactory.setSpeed(speed);
            }
            booster = false;
            shooter = false;
        }
        // Shoot if needed
        if (shotFired)
            takeAShot();
        // Mouse using
        if (mousePress) {
            mouseDirection(); // Setting the mouse direction
            int speedHolder = speed; // Changing the speed based on the distance
            long distance = (long) Math.pow(Math.abs(locY - mouseY), 2) + (long) Math.pow(Math.abs(locX - mouseX), 2);
            if (distance > 2 * Math.pow(10, 4))
                speed *= 2; // The new speed based on the distance from mouse
            vectorFactory.setSpeed(speed);
            if (distance < 64) {
                locX = mouseX;
                locY = mouseY;
            } else {
                vectorFactory.solveTheorem(1);
                if (locationController.check(locX, locY + (int) vectorFactory.y, width, height))
                    locY += (int) vectorFactory.y;
                if (locationController.check(locX + (int) vectorFactory.x, locY, width, height))
                    locX += (int) vectorFactory.x;
                speed = speedHolder; // Resetting the game speed
            }
        }
        // Up and down will change the direction
        if (keyLEFT)
            currentDirection -= 5;
        if (keyRIGHT)
            currentDirection += 5;
        vectorFactory.setTheta(currentDirection); // Setting the direction
        // Move 8 px on the current vector
        if (keyUP)
            vectorFactory.solveTheorem(1);
        if (keyDOWN)
            vectorFactory.solveTheorem(-1);
        if (keyDOWN || keyUP) {
            if (locationController.check(locX, locY + (int) vectorFactory.y, width, height))
                locY += (int) vectorFactory.y;
            if (locationController.check(locX + (int) vectorFactory.x, locY, width, height))
                locX += (int) vectorFactory.x;
        }
        locX = Math.max(locX, game.Process.GameFrame.DRAWING_START_X); // Setting the new locations based on the limits
        locX = Math.min(locX, mapColsLimit * GameMap.CHANGING_FACTOR + game.Process.GameFrame.DRAWING_START_X - width);
        locY = Math.max(locY, game.Process.GameFrame.DRAWING_START_Y);
        locY = Math.min(locY, mapRowsLimit * GameMap.CHANGING_FACTOR + game.Process.GameFrame.DRAWING_START_Y - height);
    }

    /**
     * This method will check the control buttons
     * and will send the rotation degree as int.
     *
     * @return the rotation
     */
    public int direction() { return currentDirection; }

    private void mouseDirection() {
        if (locX != mouseX)
            currentDirection = 180 + (int) Math.toDegrees(Math.atan2((locY - mouseY), (locX - mouseX)));
        else
            currentDirection = mouseY > locY ? 90 : 270;
    }

    private void takeAShot() {
        int time = (int) ((System.currentTimeMillis() - shotTimeLimit) / 1000);
        if (time > 1) {
            shotTimeLimit = System.currentTimeMillis();
            shotFired = true;
        } else
            shotFired = false;
    }

    /**
     * This method will get a box type and
     * checks if we can take it or not.
     * @param type the box type
     * @return can or not
     */
    public boolean takeBox(String type) {
        if (booster || shooter || shield)
            return false;
        else {
            pickTime = System.currentTimeMillis();
            //
            if (type.equals("boost")) {
                booster = true;
                speed *= 2;
                vectorFactory.setSpeed(speed);
            } else if (type.equals("RPG"))
                shooter = true;
            else if (type.equals("health"))
                health++;
            else if (type.equals("shield"))
                shield = true;
            //
            return true;
        }
    }

    public void setToFalse() {
        keyRIGHT = keyDOWN = keyUP = keyLEFT = shotFired = false;
    }
}