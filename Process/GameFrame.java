package game.Process;

import game.Server.DataBox;
import game.Server.MysteryBox;
import game.Server.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The window on which the rendering is performed.
 */
public class GameFrame extends JFrame {
    // fields
    public static final int DRAWING_START_X = 50, DRAWING_START_Y = 2 * DRAWING_START_X; // The drawing starting location
    private static final int GAME_HEIGHT = 1000, GAME_WIDTH = 1400; // 720p game resolution
    private String username;
    private BufferedImage image = null, RPG = null, brakeWall, unBrakeWall, explode = null, fired = null;
    private ImageIcon sand = null, grass = null;
    private BufferedImage oneWayUp, oneWayDown, twoWayUp, twoWayDown, twoWayLeft, twoWayRight, threeWayUp, threeWayDown, threeWayLeft, threeWayRight, fourWay;
    private BufferStrategy bufferStrategy;
    private GameMap gameMap; // This is the map of each game
    private CopyOnWriteArrayList<Bullet> bullets;
    private CopyOnWriteArrayList<MysteryBox> boxes;

    /**
     * The constructor of the Game frame class to set
     * the sizes and the images.
     *
     * @param title    the name of the game
     * @param tankPath the address of the tank file
     * @param username this client in game userName
     */
    public GameFrame(String title, String username, String tankPath) {
        //
        super(title);
        setLocation(20, 0);
        setResizable(false);
        setSize(GAME_WIDTH, GAME_HEIGHT);
        setIconImage(new ImageIcon("src/game/IconsInGame/Icon.png").getImage());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //
        this.username = username;
        // Opening the image
        try {
            image = ImageIO.read(new File(tankPath));
            RPG = ImageIO.read(new File("src/game/IconsInGame/Farshid/shotRed.png"));
            explode = ImageIO.read(new File("src/game/IconsInGame/Farshid/explosion2.png"));
            fired = ImageIO.read(new File("src/game/IconsInGame/Farshid/explosionSmoke4.png"));
            sand = new ImageIcon("src/game/IconsInGame/Farshid/Cell/tileSand2.png");
            grass = new ImageIcon("src/game/IconsInGame/Farshid/Cell/tileGrass2.png");
            brakeWall = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/crateWood.png"));
            unBrakeWall = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/crateMetal.png"));
            oneWayUp = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/1U.png"));
            oneWayDown = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/1D.png"));
            twoWayUp = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/2U.png"));
            twoWayDown = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/2D.png"));
            twoWayLeft = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/2L.png"));
            twoWayRight = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/2R.png"));
            threeWayUp = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/3U.png"));
            threeWayDown = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/3D.png"));
            threeWayLeft = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/3L.png"));
            threeWayRight = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/3R.png"));
            fourWay = ImageIO.read(new File("src/game/IconsInGame/Farshid/Cell/4.png"));
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
     * This must be called once after the JFrame is shown
     * and before any rendering is started.
     */
    public void initBufferStrategy() {
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
     * A setter method for setting the bullets.
     *
     * @param bullets the new set of bullets
     */
    public void setBullets(CopyOnWriteArrayList<Bullet> bullets) {
        this.bullets = bullets;
    }

    /**
     * A setter method for setting the boxes.
     *
     * @param boxes the new set of boxes
     */
    public void setBoxes(CopyOnWriteArrayList<MysteryBox> boxes) {
        this.boxes = boxes;
    }

    /**
     * Game rendering with triple-buffering using BufferStrategy.
     *
     * @param playersVector this is the list of the players in game
     */
    public void render(CopyOnWriteArrayList<User> playersVector) {
        do {
            do {
                Graphics2D graphics = (Graphics2D) bufferStrategy.getDrawGraphics();
                try {
                    doRendering(graphics, playersVector);
                } finally {
                    graphics.dispose();
                }
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
            Toolkit.getDefaultToolkit().sync();
        } while (bufferStrategy.contentsLost());
    }

    private void doRendering(Graphics2D g2d, CopyOnWriteArrayList<User> playersVector) {
        AffineTransform old = g2d.getTransform(); // Storing the old g2d transform
        // Draw background
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, GameFrame.GAME_WIDTH, DRAWING_START_Y - 10);
        // Draw Map
        // The loop of drawing
        for (int y = 0, verticalAt = DRAWING_START_Y; y < gameMap.getNumberOfRows(); y++, verticalAt += GameMap.CHANGING_FACTOR)
            for (int x = 0, horizonAt = DRAWING_START_X; x < gameMap.getNumberOfColumns(); x++, horizonAt += GameMap.CHANGING_FACTOR) {
                // Drawing the house
                g2d.fillRect(horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR);
                drawBackGround(g2d, gameMap.binaryMap[y][x], verticalAt, horizonAt);
            }
        // Drawing the boxes
        for (MysteryBox m : boxes) {
            if (m.type.equals("boost")) {
                g2d.setColor(Color.RED);
                g2d.fillRect(m.locX, m.locY, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawString("B", m.locX + 1, m.locY + 10);
            }
            if (m.type.equals("health")) {
                g2d.setColor(Color.BLUE);
                g2d.fillRect(m.locX, m.locY, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawString("+", m.locX + 1, m.locY + 10);
            }
            if (m.type.equals("RPG")) {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(m.locX, m.locY, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.drawString("X", m.locX + 1, m.locY + 10);
            }
        }
        // Drawing the players
        int counter = 2;
        boolean flag = false;
        g2d.setColor(Color.BLACK);
        g2d.drawString("Players in game : ", 40, 60);
        for (User u : playersVector) {
            DataBox dataBox = u.dataBox; // Using data box
            if (!dataBox.gameOver) {
                try {
                    image = ImageIO.read(new File(u.getImagePath()));
                } catch (IOException | NullPointerException e) {
                    try {
                        image = ImageIO.read(new File("src/game/IconsInGame/Farshid/Tank/tank_blue.png"));
                    } catch (IOException ex) {
                        e.printStackTrace();
                    }
                }
                g2d.setColor(Color.BLACK);
                g2d.drawImage(image, counter * 50 + 50, 40, dataBox.width, dataBox.height, this);
                g2d.drawString(dataBox.userName + " : " + dataBox.score, counter * 50 + 50, 50 + dataBox.height);
                counter += 2;
                // This is the rotation finding part for tank.
                int rotateDegree = dataBox.direction; // The rotation degree
                double rotation = Math.toRadians(rotateDegree);
                g2d.setColor(Color.GREEN);
                g2d.fillRect(dataBox.locX, dataBox.locY - 3, dataBox.health * 10, 3);
                g2d.setColor(Color.BLACK);
                g2d.drawString(dataBox.userName + " " + dataBox.health * 25, dataBox.locX, dataBox.locY - 8);
                //noinspection IntegerDivisionInFloatingPointContext
                g2d.rotate(rotation, dataBox.locX + dataBox.width / 2, dataBox.locY + dataBox.height / 2);
                // draw the rotated image
                g2d.drawImage(image, dataBox.locX, dataBox.locY, dataBox.width, dataBox.height, this);
                g2d.setTransform(old);
            }
            if (dataBox.userName.equals(username)) {
                flag = true;
            }
        }
        // Drawing the bullets
        for (Bullet i : bullets) {
            BufferedImage bullet = null;
            try {
                if (i.isRPG)
                    bullet = ImageIO.read(new File("src/game/IconsInGame/Farshid/shotRed.png"));
                else
                    bullet = ImageIO.read(new File(i.imagePath));
            } catch (IOException | NullPointerException e) {
                bullet = RPG;
            }
            if (i.fired)
                g2d.drawImage(fired, i.firstX - fired.getWidth() / 3, i.firstY - fired.getHeight() / 3, fired.getWidth() / 2, fired.getHeight() / 2, this);
            if (i.exploded) {
                g2d.drawImage(explode, i.locX - explode.getWidth() / 3, i.locY - explode.getHeight() / 3, explode.getWidth() / 2, explode.getHeight() / 2, this);
                continue;
            }
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
        // Draw GAME OVER
        if (!flag) {
            String str = "GAME OVER";
            g2d.setColor(new Color(100, 12, 22));
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD).deriveFont(64.0f));
            int strWidth = g2d.getFontMetrics().stringWidth(str);
            g2d.drawString(str, (GAME_WIDTH - strWidth) / 2, GAME_HEIGHT / 2);
            g2d.setTransform(old);
        }
    }

    private void drawBackGround(Graphics2D g2d, Cell cell, int verticalAt, int horizonAt) {
        if (cell.getState() == 0) {
            // Up Down Left Right
            switch (cell.status) {
                case 1111:
                    g2d.drawImage(fourWay, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                case 1011:
                    g2d.drawImage(threeWayUp, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                case 1101:
                    g2d.drawImage(threeWayRight, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                case 1110:
                    g2d.drawImage(threeWayLeft, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                case 111:
                    g2d.drawImage(threeWayDown, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                case 1100:
                    g2d.drawImage(oneWayUp, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                case 11:
                    g2d.drawImage(oneWayDown, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                case 1010:
                    g2d.drawImage(twoWayUp, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                case 101:
                    g2d.drawImage(twoWayRight, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                case 110:
                    g2d.drawImage(twoWayLeft, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                case 1001:
                    g2d.drawImage(twoWayDown, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    break;
                default:
                    if (cell.tt % 2 == 0)
                        g2d.drawImage(grass.getImage(), horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
                    else
                        g2d.drawImage(sand.getImage(), horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
            }
        } else if (cell.getState() == 1) {
            g2d.drawImage(brakeWall, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
            g2d.setColor(Color.GREEN);
            g2d.fillRect(horizonAt + 10, verticalAt + 10, (cell.health + 1) * 8, 10);
            g2d.setColor(Color.GRAY);
        } else if (cell.getState() == 2)
            g2d.drawImage(unBrakeWall, horizonAt, verticalAt, GameMap.CHANGING_FACTOR, GameMap.CHANGING_FACTOR, this);
    }
}
