package game.Control;

import game.Process.GameMap;

public class Location {

    private int topX;
    private int topY;
    private int bottomX;
    private int bottomY;

    public Location (int topX, int topY) {
        this.topX = topX;
        this.topY = topY;
        bottomX = topX + GameMap.CHANGING_FACTOR;
        bottomY = topY + GameMap.CHANGING_FACTOR;
    }

    public boolean isOverlap (int userX, int userY) {
        return ( topX < userX && bottomX > userX || topX < userX + GameMap.CHANGING_FACTOR / 2 && bottomX > userX + GameMap.CHANGING_FACTOR / 2 ) &&
                ( topY < userY && bottomY > userY || topY < userY + GameMap.CHANGING_FACTOR / 2 && bottomY > userY + GameMap.CHANGING_FACTOR / 2 );
    }

    public boolean isMatch (int inputX, int inputY) {
        return inputX == topX && inputY == topY;
    }
}
