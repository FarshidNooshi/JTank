package game.Process;

import java.io.Serializable;

/**
 * This class will chose the next location
 * where a state or a bullet needs to go.
 */
public class VectorFactory implements Serializable {

    public double x, y;
    private int speed, theta = 0;
    private double grand;

    /**
     * The constructor of the vector factory
     * to create an instance.
     *
     * @param speed the state speed
     */
    public VectorFactory(int speed) {
        this.speed = speed;
    }

    /**
     * A setter method for setting the new speed.
     *
     * @param speed the new speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * A setter method for setting the theta.
     *
     * @param newTheta the new direction
     */
    public void setTheta(int newTheta) {
        theta = newTheta;
        theoremMaker();
    }

    /**
     * This method will give grand a value
     * based on the theta.
     */
    private void theoremMaker() {
        int holder = theta % 360;
        if (holder == 90 || holder == 270 || holder == -270 || holder == -90)
            grand = 9000;
        else
            grand = Math.tan(Math.toRadians(theta));
    }

    /**
     * This method will define the next coordinates
     * based on the way. 1 is for forward and -1 for
     * backward.
     *
     * @param way the way it want to go on vector
     */
    public void solveTheorem(int way) {
        int place = theta % 360;
        if (grand == 9000) {
            x = 0;
            if (theta == 270 || theta == -90)
                y = way > 0 ? -1 * speed : speed;
            else
                y = way > 0 ? speed : -1 * speed;
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
