package game.Process;

import game.Control.Location;
import game.Control.LocationController;

public class Bullet {

    private GameMap gameMap;

    public int locX, locY, diam;
    public boolean isAlive;
    public static int speed = 4;
    private long start;

    private int mapRowsLimit, mapColsLimit;

    private boolean UP, DOWN, RIGHT, LEFT;

    public Bullet (int locX, int locY, int mapRowsLimit, int mapColsLimit, GameMap gameMap) {
        this.locX = locX + GameMap.CHANGING_FACTOR / 4;
        this.locY = locY + GameMap.CHANGING_FACTOR / 4;
        this.mapRowsLimit = mapRowsLimit;
        this.mapColsLimit = mapColsLimit;
        diam = 8;
        isAlive = true;
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

            Location location = LocationController.bulletWallCheck(locX, locY);
            if (location != null) {
                if (location.type == 1) {
                    isAlive = false;
                    gameMap.binaryMap[location.getBinaryY()][location.getBinaryX()] = 0;
                    return;
                } else {

                    int xTop = location.getTopX();
                    int yYop = location.getTopY();
                    int xDown = location.getBottomX();
                    int yDown = location.getBottomY();

                    if (xTop < locX && locX < xDown && !LEFT) {
                        if (locY <= yYop) {
                            UP = true;
                            DOWN = false;
                        } else {
                            UP = false;
                            DOWN = true;
                        }
                    } else if (yYop < locY && locY < yDown) {
                        if (locX < xTop) {
                            LEFT = true;
                            RIGHT = false;
                        } else {
                            LEFT = false;
                            RIGHT = true;
                        }
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

            if (locX == GameFrame.DRAWING_START_X) {
                LEFT = false;
                RIGHT = true;
            }
            if (locX  + diam == mapColsLimit * GameMap.CHANGING_FACTOR + GameFrame.DRAWING_START_X) {
                RIGHT = false;
                LEFT = true;
            }
            if (locY == GameFrame.DRAWING_START_Y) {
                UP = false;
                DOWN = true;
            }
            if (locY + diam == mapRowsLimit * GameMap.CHANGING_FACTOR + GameFrame.DRAWING_START_Y) {
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
