package game.Process;

import java.awt.event.*;
import java.io.Serializable;

/**
 * This class is just takes inputs from user.
 *
 */
public class Tank {
    // fields
    int width, height;
    boolean keyUP, keyDOWN, keyRIGHT, keyLEFT, mousePress, shotFired; // true if the appropriate arrow key is pressed.
    public int mouseX, mouseY; // the positions of the mouse clicked pos.
    private KeyHandler keyHandler; // for handling key events.
    private MouseHandler mouseHandler; // for handling mouse events.

    /**
     * The Tank class controller where we get the
     * users inputs.
     *
     * @param width the tank width
     * @param height the tank height
     */
    public Tank(int width, int height) {
        //
        this.width = width;
        this.height = height;
        //
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
    }

    // Getter methods
    public KeyListener getKeyListener() {
        return keyHandler;
    }
    public MouseListener getMouseListener() {
        return mouseHandler;
    }
    public MouseMotionListener getMouseMotionListener() {
        return mouseHandler;
    }

    /**
     * The keyboard handler.
     * for updating the state of the keys and maybe firing a bullet.
     */
    class KeyHandler extends KeyAdapter implements Serializable {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    keyUP = true;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    keyDOWN = true;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    keyLEFT = true;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    keyRIGHT = true;
                    break;
                case KeyEvent.VK_SPACE:
                    shotFired = true;
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    keyUP = false;
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    keyDOWN = false;
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    keyLEFT = false;
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    keyRIGHT = false;
                    break;
                case KeyEvent.VK_SPACE:
                    shotFired = false;
                    break;
            }
        }
    }

    /**
     * The mouse handler.
     */
    class MouseHandler extends MouseAdapter implements Serializable {
        @Override
        public void mousePressed(MouseEvent e) {
            mouseX = e.getX() - width / 4; // This is for placing the mouse
            mouseY = e.getY() - height / 4; // at the center of the shape
            mousePress = true;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mousePress = false;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            mouseX = e.getX() - width / 4;
            mouseY = e.getY() - height / 4;
        }
    }
}
