package game.Process;

import game.Server.DataBox;
import game.Server.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The window on which the rendering is performed.
 */
public class GameFrame extends JFrame {
    // fields
    static final int DRAWING_START_X = 40;                   // The drawing starting location
    static final int DRAWING_START_Y = 2 * DRAWING_START_X; // The drawing starting location
    private static final int GAME_HEIGHT = 720;                  // 720p game resolution
    private static final int GAME_WIDTH = 16 * GAME_HEIGHT / 9;  // wide aspect ratio
    private BufferedImage image = null;
    private BufferedImage bullet = null;
    private BufferStrategy bufferStrategy;
    private GameMap gameMap; // This is the map of each game
    private CopyOnWriteArrayList<Bullet> bullets;

    /**
     * The constructor of the Game frame class to set
     * the sizes and the images.
     *
     * @param title the name of the game
     */
    public GameFrame(String title) {
        super(title);
        setResizable(false);
        setSize(GAME_WIDTH, GAME_HEIGHT);
        setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Opening the image
        try {
            image = ImageIO.read(new File("src/game/IconsInGame/Farshid/Tank/Icon.png"));
            bullet = ImageIO.read(new File("src/game/IconsInGame/Farshid/Bullet/fireball2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A getter method for getting the selected tank
     * image.
     *
     * @return the tank image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * This must be called once after the JFrame is shown:
     * frame.setVisible(true);
     * and before any rendering is started.
     */
    public void initBufferStrategy() {
        // Triple-buffering
        createBufferStrategy(3);
        bufferStrategy = getBufferStrategy();
    }

    /**
     * To get the game map that is only exist in
     * game frame class.
     *
     * @return the game frame
     */
    public GameMap getGameMap() {
        return gameMap;
    }

    /**
     * A setter method for setting the map of the game.
     *
     * @param gameMap the game map
     */
    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    /**
     * Game rendering with triple-buffering using BufferStrategy.
     */
    public void render(Vector<User> playersVector) {
        // Render single frame
        do {
            // The following loop ensures that the contents of the drawing buffer
            // are consistent in case the underlying surface was recreated
            do {
                // Get a new graphics context every time through the loop
                // to make sure the strategy is validated
                Graphics2D graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
                try {
                    doRendering(graphics, playersVector);
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
    private void doRendering(Graphics2D g2d, Vector<User> playersVector) {
        AffineTransform old = g2d.getTransform(); // Storing the old g2d transform
        // Draw background
        g2d.setColor(Color.GRAY); // TODO: 26-Jul-20 from mapMaker import the background also write a mapMaker for creating creative maps.
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Draw Map
        // The loop of drawing
        for (int y = 0, verticalAt = DRAWING_START_Y; y < gameMap.getNumberOfRows(); y++, verticalAt += GameMap.CHANGING_FACTOR)
            for (int x = 0, horizonAt = DRAWING_START_X; x < gameMap.getNumberOfColumns(); x++, horizonAt += GameMap.CHANGING_FACTOR) {
                // Choosing the color of the house
                switch (gameMap.binaryMap[y][x].getState()) {
                    case 0:
                        g2d.setColor(Color.WHITE);
                        break;
                    case 1:
                        g2d.setColor(Color.DARK_GRAY);
                        break;
                    case 2:
                        g2d.setColor(Color.BLACK);
                        break;
                }
                // Drawing the house
                g2d.fillRect(horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR);
                if (gameMap.binaryMap[y][x].getState() == 0)
                    g2d.drawImage(gameMap.binaryMap[y][x].getIcon().getImage(), horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
            }

        for (Bullet i : bullets) {
            int rotateDegree = i.direction; // The rotation degree
            double rotation = Math.toRadians(rotateDegree);
            // Using affine to rotate
            int w = bullet.getWidth();
            int h = bullet.getHeight();
            //noinspection IntegerDivisionInFloatingPointContext
            g2d.rotate(rotation, i.locX + w / 6, i.locY + h / 6);
            g2d.drawImage(bullet, i.locX, i.locY, w / 3, h / 3, this);
            g2d.setTransform(old);
        }

        for (User u : playersVector) {
            DataBox dataBox = u.dataBox; // Using data box
            //TODO: 03-08-2020 fix the image part
            try {
                image = ImageIO.read(new File(u.imagePath));
                bullet = ImageIO.read(new File(u.bulletPath));
            } catch (IOException | NullPointerException e) {
                try {
                    image = ImageIO.read(new File("src/game/IconsInGame/Farshid/Tank/Icon.png"));
                    bullet = ImageIO.read(new File("src/game/IconsInGame/Farshid/Bullet/fireball2.png"));
                } catch (IOException ex) {
                    e.printStackTrace();
                }
            }
            // This is the rotation finding part for tank.
            int rotateDegree = dataBox.direction; // The rotation degree
            double rotation = Math.toRadians(rotateDegree);
            //noinspection IntegerDivisionInFloatingPointContext
            g2d.rotate(rotation, dataBox.locX + dataBox.width / 2, dataBox.locY + dataBox.height / 2);
            // draw the rotated image
            if (!dataBox.gameOver) {
                g2d.drawImage(image, dataBox.locX, dataBox.locY, dataBox.width, dataBox.height, this);
            }
            g2d.setTransform(old);
            // Draw GAME OVER
            if (dataBox.gameOver) {
                String str = "GAME OVER";
                g2d.setColor(new Color(100, 12, 22));
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD).deriveFont(64.0f));
                int strWidth = g2d.getFontMetrics().stringWidth(str);
                g2d.drawString(str, (GAME_WIDTH - strWidth) / 2, GAME_HEIGHT / 2);
            }
        }
    }

    public void setBullets(CopyOnWriteArrayList<Bullet> bullets) {
        this.bullets = bullets;
    }
}
