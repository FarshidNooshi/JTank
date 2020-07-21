package game.Process;

import java.util.ArrayList;
import java.util.Iterator;
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
	
	private GameFrame canvas;
	private GameState state;

	private ArrayList<Bullet> bullets;
	private ExecutorService executorService;

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
		canvas.getGameMap().setPlaces(state);
		state.setLimits(canvas.getGameMap().numberOfRows, canvas.getGameMap().numberOfColumns);
		canvas.addKeyListener(state.getKeyListener());
		canvas.addMouseListener(state.getMouseListener());
		canvas.addMouseMotionListener(state.getMouseMotionListener());
		bullets = new ArrayList<>();
		executorService = Executors.newCachedThreadPool();
	}

	@Override
	public void run() {

		boolean gameOver = false;

		while (!gameOver) {
			try {
				long start = System.currentTimeMillis();
				if (state.shotFired) {
					Bullet bullet = new Bullet(state.locX, state.locY, canvas.getGameMap());
					bullet.setDirections(state.direction());
					bullets.add(bullet);
				}
				Iterator<Bullet> iterator = bullets.iterator();
				while (iterator.hasNext()) {
					Bullet bullet = iterator.next();
					if (bullet.isAlive)
						executorService.execute(bullet.getMover());
					else
						iterator.remove();
				}
				// updating the game
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
