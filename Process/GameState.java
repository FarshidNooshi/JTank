/*** In The Name of Allah ***/
package game.Process;

import game.Control.LocationController;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * This class holds the state of game and all of its elements.
 * This class also handles user inputs, which affect the game state.
 * 
 * @author Seyed Mohammad Ghaffarian
 */
public class GameState {

	private GameMap gameMap;
	
	public static int locX, locY, diam;
	public boolean gameOver;
	
	private boolean keyUP, keyDOWN, keyRIGHT, keyLEFT;
	private boolean mousePress;
	private int mouseX, mouseY;	
	private KeyHandler keyHandler;
	private MouseHandler mouseHandler;
	
	public GameState() {
		diam = 32;
		gameOver = false;
		//
		keyUP = false;
		keyDOWN = false;
		keyRIGHT = false;
		keyLEFT = false;
		//
		mousePress = false;
		mouseX = 0;
		mouseY = 0;
		//
		keyHandler = new KeyHandler();
		mouseHandler = new MouseHandler();
	}

	public void setLocation (int x, int y) {
		locX = x;
		locY = y;
	}

	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}
	
	/**
	 * The method which updates the game state.
	 */
	public void update() {
		if (mousePress) {

			int oldY = locY;
			int oldX = locX;

			int bound = locY - mouseY;
			locY = (Math.abs(bound) > 8) ? (locY > mouseY) ? locY - 8 : locY + 8 : mouseY;

			if(!LocationController.check(locX, locY))
				locY = oldY;

			bound = locX - mouseX;
			locX = (Math.abs(bound) > 8) ? (locX > mouseX) ? locX - 8 : locX + 8 : mouseX;

			if(!LocationController.check(locX, locY))
				locX = oldX;
		}

		if (keyUP) {
			if (LocationController.check(locX, locY - 8))
				locY -= 8;
		}
		if (keyDOWN) {
			if (LocationController.check(locX, locY + 8))
				locY += 8;
		}
		if (keyLEFT) {
			if (LocationController.check(locX - 8, locY))
				locX -= 8;
		}
		if (keyRIGHT) {
			if (LocationController.check(locX + 8, locY))
				locX += 8;
		}

		locX = Math.max(locX, GameFrame.DRAWING_START_X);
		locX = Math.min(locX, gameMap.numberOfColumns * GameMap.CHANGING_FACTOR - GameMap.CHANGING_FACTOR / 2 + GameFrame.DRAWING_START_X);
		locY = Math.max(locY, GameFrame.DRAWING_START_Y);
		locY = Math.min(locY, gameMap.numberOfRows * GameMap.CHANGING_FACTOR - GameMap.CHANGING_FACTOR / 2 + GameFrame.DRAWING_START_Y);
	}
	
	
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
	 */
	class KeyHandler extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode())
			{
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
				case KeyEvent.VK_ESCAPE:
					gameOver = true;
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			switch (e.getKeyCode())
			{
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
			}
		}

	}

	/**
	 * The mouse handler.
	 */
	class MouseHandler extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			mousePress = true;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mousePress = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
		}
	}
}

