package game.Process;

public class VectorFactory {

    private final int speed;
    public double x, y;
    private int theta = 0;
    private double grand;

    public VectorFactory(int speed) {
        this.speed = speed;
    }

    public void setTheta(int newTheta) {
        theta = newTheta;
        theoremMaker();
    }

    private void theoremMaker() {
        int holder = theta % 360;
        if (holder == 90 || holder == 270 || holder == -270 || holder == -90)
            grand = 9000;
        else
            grand = Math.tan(Math.toRadians(theta));
    }

    public void solveTheorem(int way) {
        int place = theta % 360;

        if (grand == 9000) {
            x = 0;
            if (theta == 270 || theta == -90)
                y = way > 0 ? -1 * GameState.speed : GameState.speed;
            else
                y = way > 0 ? GameState.speed : -1 * GameState.speed;
            return;
        }

        x = Math.pow(speed, 2) / (1 + Math.pow(grand, 2));


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
