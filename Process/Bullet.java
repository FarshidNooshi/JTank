package game.Process;

public class Bullet {

    public int locX, locY, diam;
    public boolean isAlive;
    public static int speed = 4;
    private long start;

    private int mapRowsLimit, mapColsLimit;

    private boolean UP, DOWN, RIGHT, LEFT;

    public Bullet (int locX, int locY, int mapRowsLimit, int mapColsLimit) {
        this.locX = locX + GameMap.CHANGING_FACTOR / 4;
        this.locY = locY + GameMap.CHANGING_FACTOR / 4;
        this.mapRowsLimit = mapRowsLimit;
        this.mapColsLimit = mapColsLimit;
        diam = 8;
        isAlive = true;
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

            locX = Math.max(locX, GameFrame.DRAWING_START_X); // Setting the new locations based on the limits
            locX = Math.min(locX, mapColsLimit * GameMap.CHANGING_FACTOR - GameMap.CHANGING_FACTOR / 2 + GameFrame.DRAWING_START_X);
            locY = Math.max(locY, GameFrame.DRAWING_START_Y);
            locY = Math.min(locY, mapRowsLimit * GameMap.CHANGING_FACTOR - GameMap.CHANGING_FACTOR / 2 + GameFrame.DRAWING_START_Y);
        }
    }
}
