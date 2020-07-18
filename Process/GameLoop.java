package game.Process;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
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

	private ArrayList<Bullet> bullets;
	private ArrayList<GameState> states;
	private ExecutorService executorService;
	
	private GameFrame canvas;
	private GameState state;

	private int thisId;

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
		executorService = Executors.newCachedThreadPool();
		bullets = new ArrayList<>();
		states = new ArrayList<>();
		canvas.addKeyListener(state.getKeyListener());
		canvas.addMouseListener(state.getMouseListener());
		canvas.addMouseMotionListener(state.getMouseMotionListener());
	}

	@Override
	public void run() {
		boolean gameOver = false;

		Socket clientSocket;
		try {
			clientSocket = new Socket("127.0.0.1", 2020);
		} catch (IOException e) {
			System.out.println("Error in connecting to server :: " + e.getMessage());
			return;
		}

		InputStream inputStream;
		OutputStream outputStream;
		ObjectOutputStream objectOutputStream;
		ObjectInputStream objectInputStream;

		try {
			inputStream = clientSocket.getInputStream();
			outputStream = clientSocket.getOutputStream();
			objectOutputStream = new ObjectOutputStream(outputStream);
			objectInputStream = new ObjectInputStream(inputStream);
		} catch (IOException e) {
			System.out.println("Error in connecting to server :: " + e.getMessage());
			return;
		}

		try {
			GameMap gameMap = (GameMap) objectInputStream.readObject();
			canvas.setGameMap(gameMap);
			state.setLimits(canvas.getGameMap().numberOfRows, canvas.getGameMap().numberOfColumns); // Giving the limits
			canvas.getGameMap().setPlaces(state); // Setting the tank in the map
			thisId = inputStream.read();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		while (!gameOver) {
			try {

				long start = System.currentTimeMillis();
				// updating the game

				try {
					objectOutputStream.writeObject(state);
					int newState;
					states.clear();
					while (true) {
						newState = inputStream.read();
						if (newState == 0)
							break;
						int id = inputStream.read();
						if (id != thisId)
							states.add((GameState) objectInputStream.readObject());
						else
							state = (GameState) objectInputStream.readObject();
					}
					states.add(state);
					canvas.addKeyListener(state.getKeyListener());
					canvas.addMouseListener(state.getMouseListener());
					canvas.addMouseMotionListener(state.getMouseMotionListener());
					canvas.getGameMap().binaryMap = (int[][]) objectInputStream.readObject();
					state.setLimits(canvas.getGameMap().numberOfRows, canvas.getGameMap().numberOfColumns);
					int done;
					bullets.clear();
					while (true) {
						done = inputStream.read();
						if (done == 0)
							break;
						System.out.println(done);
						bullets.add((Bullet) objectInputStream.readObject());
					}
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
					return;
				}

				//TODO: add a update method for the bullets
				canvas.render(states, bullets);
				gameOver = state.gameOver;
				// calculating the delay for avoiding lags in the game
				long delay = (1000 / FPS) - (System.currentTimeMillis() - start);
				if (delay > 0)
					Thread.sleep(delay);
			} catch (InterruptedException ex) {
			}
		}
		canvas.render(states, bullets);
	}
}
