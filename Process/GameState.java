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
    public int locX, locY, width, height, speed = 4;
    public boolean gameOver, shotFired, waitForSecondShot;
    private int mapRowsLimit, mapColsLimit; // This is the map limits
    private int currentDirection; // This is the last rotation degree
    private boolean keyUP, keyDOWN, keyRIGHT, keyLEFT, mousePress;// true if the appropriate arrow key is pressed.
    private int mouseX, mouseY; // the positions of the mouse clicked pos.
    private KeyHandler keyHandler; // for handling key events.
    private MouseHandler mouseHandler;// for handling mouse events.
    private long shotTimeLimit;// just for remembering the timeLimit of the shots. ;)
    private int roundCounter; // For second bullet shooting
    private VectorFactory vectorFactory; // Each state has its own vector factory

    /**
     * The game state constructor.
     */
    public GameState() {
        gameOver = false;
        shotFired = false;
        currentDirection = 0;
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
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        //
        vectorFactory = new VectorFactory(speed);
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
        // The shooting statements
        shotFired = false;
        if (waitForSecondShot && roundCounter > 3) {
            shotFired = true;
            waitForSecondShot = false;
        }
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
                if (LocationController.check(locX, locY + (int) vectorFactory.y, width, height))
                    locY += (int) vectorFactory.y;
                if (LocationController.check(locX + (int) vectorFactory.x, locY, width, height))
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
            if (LocationController.check(locX, locY + (int) vectorFactory.y, width, height))
                locY += (int) vectorFactory.y;
            if (LocationController.check(locX + (int) vectorFactory.x, locY, width, height))
                locX += (int) vectorFactory.x;
        }

        locX = Math.max(locX, game.Process.GameFrame.DRAWING_START_X); // Setting the new locations based on the limits
        locX = Math.min(locX, mapColsLimit * GameMap.CHANGING_FACTOR + game.Process.GameFrame.DRAWING_START_X - width);
        locY = Math.max(locY, game.Process.GameFrame.DRAWING_START_Y);
        locY = Math.min(locY, mapRowsLimit * GameMap.CHANGING_FACTOR + game.Process.GameFrame.DRAWING_START_Y - height);
        roundCounter++; // Need to count for bullet shooting
    }

    /**
     * This method will check the control buttons
     * and will send the rotation degree as int.
     *
     * @return the rotation
     */
    public int direction() {
        return currentDirection;
    }

    /*
        This method will calculate the direction between the
        mouse location and the state location.
     */
    private void mouseDirection() {
        if (locX != mouseX)
            currentDirection = 180 + (int) Math.toDegrees(Math.atan2((locY - mouseY), (locX - mouseX)));
        else
            currentDirection = mouseY > locY ? 90 : 270;
    }


    public KeyListener getKeyListener() {
        return keyHandler;
    }

    public MouseListener getMouseListener() {
        return mouseHandler;
    }

    public MouseMotionListener getMouseMotionListener() {
        return mouseHandler;
    }

    /**
     * The keyboard handler.
     * for updating the state of the keys and maybe firing a bullet.
     */
    class KeyHandler extends KeyAdapter implements Serializable {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    keyUP = true;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    keyDOWN = true;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    keyLEFT = true;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    keyRIGHT = true;
                    break;
                case KeyEvent.VK_SPACE:
                    takeAShot();
                    break;
                case KeyEvent.VK_ESCAPE:
                    gameOver = true;
                    break;
            }
        }

        /**
         * This method will shot a bullet and
         * will set the other parameters to shot
         * the second bullet.
         */
        private void takeAShot() {
            int time = (int) ((System.currentTimeMillis() - shotTimeLimit) / 1000);
            if (time > 1) {
                shotTimeLimit = System.currentTimeMillis();
                shotFired = true;
                waitForSecondShot = true;
                roundCounter = 0;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    keyUP = false;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    keyDOWN = false;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    keyLEFT = false;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    keyRIGHT = false;
                    break;
            }
        }
    }

    /**
     * The mouse handler.
     */
    class MouseHandler extends MouseAdapter implements Serializable {
        @Override
        public void mousePressed(MouseEvent e) {
            mouseX = e.getX() - width / 4; // This is for placing the mouse
            mouseY = e.getY() - height / 4; // at the center of the shape
            mousePress = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePress = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            mouseX = e.getX() - width / 4;
            mouseY = e.getY() - height / 4;
        }
    }
}