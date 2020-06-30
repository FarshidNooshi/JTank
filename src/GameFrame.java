/*** In The Name of Allah ***/
package game.Begin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * The window on which the rendering is performed.
 * This example uses the modern BufferStrategy approach for double-buffering, 
 * actually it performs triple-buffering!
 * For more information on BufferStrategy check out:
 *    http://docs.oracle.com/javase/tutorial/extra/fullscreen/bufferstrategy.html
 *    http://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferStrategy.html
 * 
 * @author Seyed Mohammad Ghaffarian
 */
public class GameFrame extends JFrame {
	
	public static final int GAME_HEIGHT = 720;                  // 720p game resolution
	public static final int GAME_WIDTH = 16 * GAME_HEIGHT / 9;  // wide aspect ratio

	public static final int DRAWING_START_X = 40;
	public static final int DRAWING_START_Y = 80;

	//uncomment all /*...*/ in the class for using Tank icon instead of a simple circle
	private BufferedImage image;

	private long lastRender;
	private ArrayList<Float> fpsHistory;

	private BufferStrategy bufferStrategy;

	private GameMap gameMap;
	
	public GameFrame(String title) {
		super(title);
		setResizable(false);
		setSize(GAME_WIDTH, GAME_HEIGHT);
		lastRender = -1;
		fpsHistory = new ArrayList<>(100);

		try{
			image = ImageIO.read(new File("Icon.png"));
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	/**
	 * This must be called once after the JFrame is shown:
	 *    frame.setVisible(true);
	 * and before any rendering is started.
	 */
	public void initBufferStrategy() {
		// Triple-buffering
		createBufferStrategy(3);
		bufferStrategy = getBufferStrategy();
	}

	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}

	public GameMap getGameMap() {
		return gameMap;
	}

	/**
	 * Game rendering with triple-buffering using BufferStrategy.
	 */
	public void render(GameState state) {
		// Render single frame
		do {
			// The following loop ensures that the contents of the drawing buffer
			// are consistent in case the underlying surface was recreated
			do {
				// Get a new graphics context every time through the loop
				// to make sure the strategy is validated
				Graphics2D graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
				try {
					doRendering(graphics, state);
				} finally {
					// Dispose the graphics
					graphics.dispose();
				}
				// Repeat the rendering if the drawing buffer contents were restored
			} while (bufferStrategy.contentsRestored());

			// Display the buffer
			bufferStrategy.show();
			// Tell the system to do the drawing NOW;
			// otherwise it can take a few extra ms and will feel jerky!
			Toolkit.getDefaultToolkit().sync();

		// Repeat the rendering if the drawing buffer was lost
		} while (bufferStrategy.contentsLost());
	}
	
	/**
	 * Rendering all game elements based on the game state.
	 */
	private void doRendering(Graphics2D g2d, GameState state) {
		// Draw background
		g2d.setColor(Color.GRAY);
		g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		// Draw Map
		int horizonAt = DRAWING_START_X;
		int verticalAt = DRAWING_START_Y;

		for (int y = 0; y < gameMap.numberOfRows; y++) {
			for (int x = 0; x < gameMap.numberOfColumns; x++) {

				switch (gameMap.binaryMap[y][x]) {
					case 0:
						g2d.setColor(Color.WHITE);
						break;
					case 1:
						g2d.setColor(Color.BLACK);
						break;
					case 2:
						g2d.setColor(Color.DARK_GRAY);
						break;
				}

				g2d.fillRect(horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR);
				horizonAt += GameMap.CHANGING_FACTOR;
			}

			horizonAt = DRAWING_START_X;
			verticalAt += GameMap.CHANGING_FACTOR;
		}
		//g2d.fillOval(state.locX, state.locY, state.diam, state.diam);

		//g2d.drawImage(image,state.locX,state.locY,null);
		g2d.drawImage(image, state.locX, state.locY, 40,40, null);


		// Print FPS info
		long currentRender = System.currentTimeMillis();
		if (lastRender > 0) {
			fpsHistory.add(1000.0f / (currentRender - lastRender));
			if (fpsHistory.size() > 100) {
				fpsHistory.remove(0); // remove oldest
			}
			float avg = 0.0f;
			for (float fps : fpsHistory) {
				avg += fps;
			}
			avg /= fpsHistory.size();
			String str = String.format("Average FPS = %.1f , Last Interval = %d ms",
					avg, (currentRender - lastRender));
			g2d.setColor(Color.CYAN);
			g2d.setFont(g2d.getFont().deriveFont(18.0f));
			int strWidth = g2d.getFontMetrics().stringWidth(str);
			int strHeight = g2d.getFontMetrics().getHeight();
			g2d.drawString(str, (GAME_WIDTH - strWidth) / 2, strHeight+50);
		}
		lastRender = currentRender;
		// Print user guide
		String userGuide
				= "Use the MOUSE or ARROW KEYS to move the BALL. "
				+ "Press ESCAPE to end the game.";
		g2d.setFont(g2d.getFont().deriveFont(18.0f));
		g2d.drawString(userGuide, 10, GAME_HEIGHT - 10);
		// Draw GAME OVER
		if (state.gameOver) {
			String str = "GAME OVER";
			g2d.setColor(Color.WHITE);
			g2d.setFont(g2d.getFont().deriveFont(Font.BOLD).deriveFont(64.0f));
			int strWidth = g2d.getFontMetrics().stringWidth(str);
			g2d.drawString(str, (GAME_WIDTH - strWidth) / 2, GAME_HEIGHT / 2);
		}
	}
	
}
