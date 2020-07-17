package game.Process;

import game.Control.LocationController;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A very simple structure for the main game loop.
 * THIS IS NOT PERFECT, but works for most situations.
 * Note that to make this work, none of the 2 methods 
 * in the while loop (update() and render()) should be 
 * long running! Both must execute very quickly, without 
 * any waiting and blocking!
 * 
 * Detailed discussion on different game loop design
 * patterns is available in the following link:
 *    http://gameprogrammingpatterns.com/game-loop.html
 *
 */
public class GameLoop implements Runnable {
	
	/**
	 * Frame Per Second.
	 * Higher is better, but any value above 24 is fine.
	 */
	public static final int FPS = 30;

	private CopyOnWriteArrayList<Bullet> bullets;
	private ExecutorService executorService;
	
	private GameFrame canvas;
	private GameState state;

	/**
	 * The constructor of the game loop.
	 * To create the frame of the game.
	 *
	 * @param frame the game frame
	 */
	public GameLoop(GameFrame frame) {
		canvas = frame;
	}
	
	/**
	 * This must be called before the game loop starts.
	 */
	public void init() {
		state = new GameState(); // Creating the states
		state.setLimits(canvas.getGameMap().numberOfRows, canvas.getGameMap().numberOfColumns); // Giving the limits
		bullets = new CopyOnWriteArrayList<>();
		executorService = Executors.newCachedThreadPool();
		canvas.getGameMap().setPlaces(state); // Setting the tank in the map
		canvas.addKeyListener(state.getKeyListener());
		canvas.addMouseListener(state.getMouseListener());
		canvas.addMouseMotionListener(state.getMouseMotionListener());
	}

	@Override
	public void run() {
		boolean gameOver = false;
		while (!gameOver) {
			try {
				long start = System.currentTimeMillis();
				// updating the game
				if (state.shotFired) {
					Bullet bullet = new Bullet(state.locX, state.locY, canvas.getGameMap());
					bullets.add(bullet);
					bullet.setDirections(state.direction());
				}
				// The bullets loop
				Iterator<Bullet> iterator = bullets.iterator();
				while (iterator.hasNext()) {
					Bullet bullet = iterator.next();
					if (LocationController.tankGotShot(bullet.locX + bullet.diam / 2, bullet.locY + bullet.diam / 2, state.locX, state.locY) && !bullet.justShot) {
						state.gameOver = true;
						bullet.isAlive = false;
					}
					if (bullet.isAlive)
						executorService.execute(bullet.getMover());
					else
						bullets.remove(bullet);
				}
				state.update();
				//TODO: add a update method for the bullets
				canvas.render(state, bullets);
				gameOver = state.gameOver;
				// calculating the delay for avoiding lags in the game
				long delay = (1000 / FPS) - (System.currentTimeMillis() - start);
				if (delay > 0)
					Thread.sleep(delay);
			} catch (InterruptedException ex) {
			}
		}
		canvas.render(state, bullets);
	}
}
