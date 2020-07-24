package game.Process;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The window on which the rendering is performed.
 * This example uses the modern BufferStrategy approach for double-buffering,
 * actually it performs triple-buffering!
 * For more information on BufferStrategy check out:
 * http://docs.oracle.com/javase/tutorial/extra/fullscreen/bufferstrategy.html
 * http://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferStrategy.html
 */
public class GameFrame extends JFrame {

    public static final int GAME_HEIGHT = 720;                  // 720p game resolution
    public static final int GAME_WIDTH = 16 * GAME_HEIGHT / 9;  // wide aspect ratio

    public static final int DRAWING_START_X = 40;                   // The drawing starting location
    public static final int DRAWING_START_Y = 2 * DRAWING_START_X; // The drawing starting location

    //uncomment all /*...*/ in the class for using Tank icon instead of a simple circle
    private BufferedImage image = null;
    private BufferedImage bullet = null;
    private int counter = 0;

    private long lastRender;
    private ArrayList<Float> fpsHistory;

    private BufferStrategy bufferStrategy;

    private GameMap gameMap; // This is the map of each game

    /**
     * The constructor of the Game frame class to set
     * the sizes and the images.
     *
     * @param title the name of the game
     */
    public GameFrame(String title) throws InterruptedException {
        super(title);
        setResizable(false);
        setSize(GAME_WIDTH, GAME_HEIGHT);
        setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
        lastRender = -1;
        fpsHistory = new ArrayList<>(100);
        // Opening the image
        try {
            image = ImageIO.read(new File("src/game/IconsInGame/Icon.png"));
            bullet = ImageIO.read(new File("./src/game/IconsInGame/fireball2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int response = JOptionPane.showConfirmDialog(this, "do you want to customize your tank ?");
        if (response == JOptionPane.YES_OPTION) {
            this.setVisible(false);
            JFrame ask = new JFrame("Choose tank model");
            JPanel c = new MainPanel(new ImageIcon("src/game/IconsInGame/Farshid/background.png").getImage());
            JLabel logo = new JLabel(new ImageIcon("src/game/IconsInGame/Logo.png"));
            c.add(logo);
            logo.setLocation(300, 0);
            logo.setSize(700, 150);
            ask.setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
            File file = new File("src/game/IconsInGame/Farshid/Tank");
            c.setLayout(null);
            int tmp = 0;
            ask.setExtendedState(Frame.MAXIMIZED_BOTH);
            for (String name : Objects.requireNonNull(file.list())) {
                JButton button = new JButton(new ImageIcon("src/game/IconsInGame/Farshid/Tank/" + name));
                button.setLocation(160 * (tmp / 5), 100 + 110 * (tmp % 5));
                button.setSize(150, 100);
                c.add(button);
                tmp++;
                button.addActionListener(e -> {
                    try {
                        image = ImageIO.read(new File("src/game/IconsInGame/Farshid/Tank/" + name));
                        counter++;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (counter == 2) {
                        ask.setVisible(false);
                        this.setVisible(true);
                    }
                });
            }
            file = new File("src/game/IconsInGame/Farshid/Bullet");
            tmp = 0;
            for (String name : Objects.requireNonNull(file.list())) {
                JButton button = new JButton(new ImageIcon("src/game/IconsInGame/Farshid/Bullet/" + name));
                button.setLocation(1250 - 40 * (tmp / 5), 100 + 55 * (tmp % 5));
                button.setSize(30, 45);
                c.add(button);
                tmp++;
                button.addActionListener(e -> {
                    try {
                        bullet = ImageIO.read(new File("src/game/IconsInGame/Farshid/Bullet/" + name));
                        counter++;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (counter == 2) {
                        ask.setVisible(false);
                        this.setVisible(true);
                    }
                });
            }
            ask.add(c);
            ask.setAlwaysOnTop(true);
            ask.setLocationRelativeTo(null);
            ask.setVisible(true);
        }
    }

    public BufferedImage getImage () {
        return image;
    }

    public BufferedImage getBulletImage () {
        return bullet;
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
    public void render(GameState state, ArrayList<Bullet> bullets) {
        // Render single frame
        do {
            // The following loop ensures that the contents of the drawing buffer
            // are consistent in case the underlying surface was recreated
            do {
                // Get a new graphics context every time through the loop
                // to make sure the strategy is validated
                Graphics2D graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
                try {
                    doRendering(graphics, state, bullets);
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
    private void doRendering(Graphics2D g2d, GameState state, ArrayList<Bullet> bullets) {

        AffineTransform old = g2d.getTransform();

        // Draw background
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Draw Map
        int horizonAt = DRAWING_START_X; // To locate the starting position
        int verticalAt = DRAWING_START_Y;
        // The loop of drawing
        for (int y = 0; y < gameMap.numberOfRows; y++) {

            for (int x = 0; x < gameMap.numberOfColumns; x++) {
                // Choosing the color of the house
                switch (gameMap.binaryMap[y][x]) {
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
                horizonAt += GameMap.CHANGING_FACTOR;
            }

            horizonAt = DRAWING_START_X;
            verticalAt += GameMap.CHANGING_FACTOR;
        }

        for (Bullet i : bullets) {
            int rotateDegree = i.direction; // The rotation degree
            double rotation = Math.toRadians(rotateDegree);
            // Using affine to rotate
            int w = bullet.getWidth();
            int h = bullet.getHeight();
            g2d.rotate(rotation, i.locX + w / 8, i.locY + h / 8);
            g2d.drawImage(bullet, i.locX, i.locY, w / 4, h / 4, null);
            g2d.setTransform(old);
        }


        // This is the rotation finding part
        int rotateDegree = state.direction(); // The rotation degree
        double rotation = Math.toRadians(rotateDegree);
        // Using affine to rotate
        g2d.rotate(rotation, state.locX + state.width / 2, state.locY + state.height / 2);
        // draw the rotated image
        if (!state.gameOver)
            g2d.drawImage(image, state.locX, state.locY, state.width, state.height, null);
        g2d.setTransform(old);

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
            g2d.drawString(str, (GAME_WIDTH - strWidth) / 2, strHeight + 50);
        }
        lastRender = currentRender;
        // Print user guide
        String userGuide = "Use the MOUSE or ARROW KEYS to move the BALL. "
                + "Press ESCAPE to end the game.";
        g2d.setFont(g2d.getFont().deriveFont(18.0f));
        g2d.drawString(userGuide, 10, GAME_HEIGHT - 10);
        // Draw GAME OVER
        if (state.gameOver) {
            String str = "GAME OVER";
            g2d.setColor(new Color(100, 12, 22));
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD).deriveFont(64.0f));
            int strWidth = g2d.getFontMetrics().stringWidth(str);
            g2d.drawString(str, (GAME_WIDTH - strWidth) / 2, GAME_HEIGHT / 2);
        }
    }

    private static class MainPanel extends JPanel {
        private Image bg;

        MainPanel(Image bg) {
            this.bg = bg;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
