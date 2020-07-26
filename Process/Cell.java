package game.Process;

import javax.swing.*;

public class Cell {
    private int state;
    private ImageIcon icon;

    public Cell(int state, int tt) {
        this.state = state;
        if (tt % 2 == 1)
            icon = new ImageIcon("src/game/IconsInGame/Farshid/Cell/tileSand2.png");
        else
            icon = new ImageIcon("src/game/IconsInGame/Farshid/Cell/tileGrass2.png");
    }

    public void setIcon(int tt) {
        if (tt % 2 == 1)
            icon = new ImageIcon("src/game/IconsInGame/Farshid/Cell/tileSand2.png");
        else
            icon = new ImageIcon("src/game/IconsInGame/Farshid/Cell/tileGrass2.png");
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
