package game.Process;

public class Bullet {

    public int locX, locY, diam;
    public boolean isAlive;
    public static int speed = 4;

    private int mapRowsLimit, mapColsLimit;

    private boolean UP, DOWN, RIGHT, LEFT;

    public Bullet (int locX, int locY, int mapRowsLimit, int mapColsLimit) {
        this.locX = locX;
        this.locY = locY;
        this.mapRowsLimit = mapRowsLimit;
        this.mapColsLimit = mapColsLimit;
        diam = 32;
    }

    public void setDirections (boolean UP, boolean DOWN, boolean RIGHT, boolean LEFT) {
        this.UP = UP;
        this.DOWN = DOWN;
        this.RIGHT = RIGHT;
        this.LEFT = LEFT;
    }

    public BulletMove getMover () {
        return new BulletMove();
    }

    class BulletMove implements Runnable {

        @Override
        public void run() {
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
