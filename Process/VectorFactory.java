package game.Process;

public class VectorFactory {

<<<<<<< HEAD
    private static int theta = 0;
    private static double grand;
    static double x, y;

    public static void setTheta (int newTheta) {
=======
    private int theta = 0;
    private double grand;
    public double x, y;
    private final int speed;

    public VectorFactory (int speed) {
        this.speed = speed;
    }

    public void setTheta (int newTheta) {
>>>>>>> add_bullets
        theta = newTheta;
        theoremMaker();
    }

<<<<<<< HEAD
    private static void theoremMaker () {
=======
    private void theoremMaker () {
>>>>>>> add_bullets
        int holder = theta % 360;
        if (holder == 90 || holder == 270 || holder == -270 || holder == -90)
            grand = 9000;
        else
            grand = Math.tan(Math.toRadians(theta));
    }

<<<<<<< HEAD
    public static void solveTheorem (int way) {
=======
    public void solveTheorem (int way) {
>>>>>>> add_bullets
        int place = theta % 360;

        if (grand == 9000) {
            x = 0;
            if (theta == 270 || theta == -90)
<<<<<<< HEAD
                y = way > 0 ? -1 * GameState.speed : GameState.speed;
            else
                y = way > 0 ? GameState.speed : -1 * GameState.speed;
            return;
        }

        x = Math.pow(GameState.speed, 2) / (1 + Math.pow(grand, 2));
=======
                y = way > 0 ? -1 * speed : speed;
            else
                y = way > 0 ? speed : -1 * speed;
            return;
        }

        x = Math.pow(speed, 2) / (1 + Math.pow(grand, 2));
>>>>>>> add_bullets

        if (place > 90 && place < 270 || place < -90 && place > -270) {
            if (way > 0)
                x = -1 * Math.sqrt(x);
            else
                x = Math.sqrt(x);
        } else {
            if (way > 0)
                x = Math.sqrt(x);
            else
                x = -1 * Math.sqrt(x);
        }

        y = x * grand;
    }
}
