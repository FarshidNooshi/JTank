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
    public int health;
    private ImageIcon icon;

    /**
     * The main constructor of the cell class.
     * @param state the type of the cell
     * @param tt a random number to show the image
     */
    public Cell(int state, int tt, int health) {
        this.state = state;
        this.health = health;
        if (tt % 2 == 1)
            icon = new ImageIcon("src/game/IconsInGame/Farshid/Cell/tileSand2.png");
        else
            icon = new ImageIcon("src/game/IconsInGame/Farshid/Cell/tileGrass2.png");
    }

    /**
     * Setter of the icon method.
     * @param tt the random number
     */
    public void setIcon(int tt) {
        if (tt % 2 == 1)
            icon = new ImageIcon("src/game/IconsInGame/Farshid/Cell/tileSand2.png");
        else
            icon = new ImageIcon("src/game/IconsInGame/Farshid/Cell/tileGrass2.png");
    }

    /**
     * Getter of the icon.
     * @return the image of the cell
     */
    public ImageIcon getIcon() {
        return icon;
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
