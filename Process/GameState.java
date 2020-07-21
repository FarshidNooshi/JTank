package game.Process;

import game.Control.LocationController;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

/**
 * This class holds the state of game and all of its elements.
 * This class also handles user inputs, which affect the game state.
 *
 */
public class GameState implements Serializable {

	private int mapRowsLimit, mapColsLimit; // This is the map limits
	
	public int locX, locY, diam;
	public boolean gameOver;
	public static int speed = 4;
	private int currentDirection; // This is the last rotation degree

	private boolean keyUP, keyDOWN, keyRIGHT, keyLEFT;
	private boolean mousePress;
	private int mouseX, mouseY;	
	private KeyHandler keyHandler;
	private MouseHandler mouseHandler;
	public boolean shotFired, waitForSecondShot;
	private long shotTimeLimit;
	private int roundCounter;

	private VectorFactory vectorFactory;
	
	public GameState() {
		diam = 32;
		gameOver = false;
		shotFired = false;
		currentDirection = 0;
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
		//
		vectorFactory = new VectorFactory(speed);
	}

	/**
	 * A setter method for setting the locations.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void setLocation (int x, int y) {
		locX = x;
		locY = y;
	}

	/**
	 * This is a method for setting the borders
	 * limits.
	 *
	 * @param mapRows the rows limit
	 * @param mapCols the cols limit
	 */
	public void setLimits(int mapRows, int mapCols) {
		mapRowsLimit = mapRows;
		mapColsLimit = mapCols;
	}
	
	/**
	 * The method which updates the game state.
	 */
	public void update() {

		shotFired = false;
		if (waitForSecondShot && roundCounter > 3) {
			shotFired = true;
			waitForSecondShot = false;
		}

		if (mousePress)
		{
			mouseDirection();

			int speedHolder = GameState.speed;
			long distance = (long) Math.pow(Math.abs(locY - mouseY), 2) + (long) Math.pow(Math.abs(locX - mouseX), 2);

			if (distance > 2 * Math.pow(10, 4))
				GameState.speed *= 2; // The new speed based on the distance from mouse

			if (distance < 64)
			{
				locX = mouseX;
				locY = mouseY;
			} else {

<<<<<<< HEAD
				VectorFactory.solveTheorem(1);

				if (LocationController.check(locX + (int) VectorFactory.x, locY + (int) VectorFactory.y))
				{
					locY += (int) VectorFactory.y;
					locX += (int) VectorFactory.x;
=======
				vectorFactory.solveTheorem(1);

				if (LocationController.check(locX + (int) vectorFactory.x, locY + (int) vectorFactory.y))
				{
					locY += (int) vectorFactory.y;
					locX += (int) vectorFactory.x;
>>>>>>> add_bullets
				}
			}
			GameState.speed = speedHolder; // Resetting the game speed
		}

		if (keyUP)
<<<<<<< HEAD
<<<<<<< HEAD
			currentDirection -= 1;
		if (keyDOWN)
			currentDirection += 1;
=======
			currentDirection -= 5;
		if (keyDOWN)
			currentDirection += 5;
>>>>>>> making_360_direction

		VectorFactory.setTheta(currentDirection);

		if (keyLEFT)
<<<<<<< HEAD
			TankLine.solveTheorem(-1);
		if (keyRIGHT)
			TankLine.solveTheorem(1);

		if (keyRIGHT || keyLEFT) {
			if (LocationController.check(locX + (int) TankLine.x, locY + (int) TankLine.y)) {
				locY += TankLine.y;
				locX += TankLine.x;
=======
			VectorFactory.solveTheorem(-1);
		if (keyRIGHT)
			VectorFactory.solveTheorem(1);

		if (keyLEFT || keyRIGHT)
		{
			if (LocationController.check(locX + (int) VectorFactory.x, locY + (int) VectorFactory.y))
			{
				locY += (int) VectorFactory.y;
				locX += (int) VectorFactory.x;
>>>>>>> making_360_direction
=======
			currentDirection -= 5;
		if (keyDOWN)
			currentDirection += 5;

		vectorFactory.setTheta(currentDirection);

		if (keyLEFT)
			vectorFactory.solveTheorem(-1);
		if (keyRIGHT)
			vectorFactory.solveTheorem(1);

		if (keyLEFT || keyRIGHT)
		{
			if (LocationController.check(locX + (int) vectorFactory.x, locY + (int) vectorFactory.y))
			{
				locY += (int) vectorFactory.y;
				locX += (int) vectorFactory.x;
>>>>>>> add_bullets
			}
		}

		locX = Math.max(locX, GameFrame.DRAWING_START_X); // Setting the new locations based on the limits
		locX = Math.min(locX, mapColsLimit * GameMap.CHANGING_FACTOR - GameMap.CHANGING_FACTOR / 2 + GameFrame.DRAWING_START_X);
		locY = Math.max(locY, GameFrame.DRAWING_START_Y);
		locY = Math.min(locY, mapRowsLimit * GameMap.CHANGING_FACTOR - GameMap.CHANGING_FACTOR / 2 + GameFrame.DRAWING_START_Y);
		roundCounter++;
	}

	/**
	 * This method will check the control buttons
	 * and will send the rotation degree as int.
	 *
	 * @return the rotation
	 */
	public int direction () {
		return currentDirection;
	}

	private void mouseDirection () {
		if ( locX != mouseX )
			currentDirection = 180 + (int) Math.toDegrees(Math.atan2((locY - mouseY), (locX - mouseX)));
		else
			currentDirection = mouseY > locY ? 90 : 270;
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
	class KeyHandler extends KeyAdapter implements Serializable {

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
				case KeyEvent.VK_SPACE:
					takeAShot();
					break;
				case KeyEvent.VK_ESCAPE:
					gameOver = true;
					break;
			}
		}

		private void takeAShot () {
			int time = (int) (( System.currentTimeMillis() - shotTimeLimit ) / 1000);
			if (time > 1) {
				shotTimeLimit = System.currentTimeMillis();
				shotFired = true;
				waitForSecondShot = true;
				roundCounter = 0;
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
	class MouseHandler extends MouseAdapter implements Serializable {

		@Override
		public void mousePressed(MouseEvent e) {
			mouseX = e.getX() - GameMap.CHANGING_FACTOR / 4; // This is for placing the mouse
			mouseY = e.getY() - GameMap.CHANGING_FACTOR / 4; // at the center of the shape
			mousePress = true;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			mousePress = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseX = e.getX() - GameMap.CHANGING_FACTOR / 4;
			mouseY = e.getY() - GameMap.CHANGING_FACTOR / 4;
		}
	}
}