package game.Process;

import game.Control.Location;
import game.Control.LocationController;

public class Bullet {

    private GameMap gameMap;

    //Location fields
    public int locX, locY, diam;
    private int firstX, firstY;
    //Status fields
    public boolean isAlive, justShot;
    //Speed and time fields
    public static int speed = 8;
    private long start;

    private int mapRowsLimit, mapColsLimit;

    private boolean UP, DOWN, RIGHT, LEFT;

    public Bullet (int locX, int locY, int mapRowsLimit, int mapColsLimit, GameMap gameMap) {
        this.locX = locX + GameMap.CHANGING_FACTOR / 4;
        this.locY = locY + GameMap.CHANGING_FACTOR / 4;
        firstX = this.locX;
        firstY = this.locY;
        this.mapRowsLimit = mapRowsLimit;
        this.mapColsLimit = mapColsLimit;
        diam = 8;
        isAlive = true;
        justShot = true;
        this.gameMap = gameMap;
        start = System.currentTimeMillis();
    }

    public void setDirections (int directions) {

        switch (directions) {
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

    public BulletMove getMover () {
        return new BulletMove();
    }

    class BulletMove implements Runnable {

        @Override
        public void run() {

            int time = (int) ((System.currentTimeMillis() - start) / 1000);
            if (time >= 4)
                isAlive = false;
            if (justShot) {
                if (Math.abs(firstX - locX) > GameMap.CHANGING_FACTOR / 5 || Math.abs(firstY - locY) > GameMap.CHANGING_FACTOR / 5)
                    justShot = false;
            }

            Location location = LocationController.bulletWallCheck(locX, locY);
            if (location != null) {
                if (location.type == 1) {
                    isAlive = false;
                    gameMap.binaryMap[location.getBinaryY()][location.getBinaryX()] = 0;
                    return;
                } else {

                    int xTop = location.getTopX() + diam;
                    int yYop = location.getTopY() + diam;
                    int xDown = location.getBottomX() - diam;
                    int yDown = location.getBottomY() - diam;
                    int centerX = (xTop + xDown) / 2;
                    int centerY = (yYop + yDown) / 2;

                    if (locY + diam / 2 > centerY && locX + diam / 2 <= xDown && locX + diam / 2 >= xTop) {
                        UP = false;
                        DOWN = true;
                    }
                    if (locX + diam / 2 < centerX && locY + diam / 2 <= yDown && locY + diam / 2 >= yYop) {
                        LEFT = true;
                        RIGHT = false;
                    }
                    if (locY + diam / 2 < centerY && locX + diam / 2 < xDown && locX + diam / 2 > xTop) {
                        UP = true;
                        DOWN = false;
                    }
                    if (locX + diam / 2 > centerX && locY + diam / 2 < yDown && locY + diam / 2 > yYop) {
                        LEFT = false;
                        RIGHT = true;
                    }
                }
            }

            if (UP)
            {
                locY -= speed;
            }
            if (DOWN)
            {
                locY += speed;
            }
            if (LEFT)
            {
                locX -= speed;
            }
            if (RIGHT)
            {
                locX += speed;
            }

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
    }
}
