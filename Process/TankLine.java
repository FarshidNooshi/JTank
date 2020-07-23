package game.Process;

public class TankLine {

    static double x, y;
    private static int theta = 0;
    private static double grand;

    public static void setTheta(int newTheta) {
        theta = newTheta;
        theoremMaker();
    }

    private static void theoremMaker() {
        int holder = theta % 360;
        if (holder == 90 || holder == 270 || holder == -270 || holder == -90)
            grand = 9000;
        else
            grand = Math.tan(Math.toRadians(theta));
    }

    public static void solveTheorem(int way) {
        if (grand == 9000) {
            x = 0;
            y = (way > 0) ? GameState.speed : -1 * GameState.speed;
            return;
        }
        x = Math.pow(GameState.speed, 2) / (1 + Math.pow(grand, 2));
        if (way > 0)
            x = Math.sqrt(x);
        else
            x = -1 * Math.sqrt(x);
        y = x * grand;
    }
}
