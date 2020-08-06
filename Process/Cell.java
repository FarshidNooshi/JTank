package game.Process;

import javax.swing.*;
import java.io.Serializable;

/**
 * This is a single cell house in 2d array
 * where we keep the info about a house.
 * The type and the image to show.
 *
 */
public class Cell implements Serializable {
    // Privet fields
    private int state;
    public int health, status = 0;
    public int tt;

    /**
     * The main constructor of the cell class.
     * @param state the type of the cell
     * @param tt a random number to show the image
     */
    public Cell(int state, int tt, int health) {
        this.state = state;
        this.health = health;
        this.tt = tt;
    }

    /**
     * Setter of the icon method.
     * @param tt the random number
     */
    public void setIcon(int tt) {
        this.tt = tt;
    }

    /**
     * Getter of the cell type.
     * @return the type of the cell
     */
    public int getState() {
        return state;
    }

    /**
     * Setter method of the type.
     * @param state the new type
     */
    public void setState(int state) {
        this.state = state;
    }
}
