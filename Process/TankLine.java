package game.Process;

public class TankLine {

    private static int theta = 0;
    private static double grand;
    static double x, y;

    public static void setTheta (int newTheta) {
        theta = newTheta;
        theoremMaker();
    }

    private static void theoremMaker () {
        int holder = theta % 360;
        if (holder == 90 || holder == 270 || holder == -270 || holder == -90)
            grand = 88;
        else
            grand = Math.tan(Math.toRadians(theta));
    }

    public static void solveTheorem (int way) {
        x = GameState.speed / (1 + Math.pow(grand, 2));
        if (way > 0)
            x = Math.sqrt(x);
        else
            x = -1 * Math.sqrt(x);
        y = x * grand;
    }
}
