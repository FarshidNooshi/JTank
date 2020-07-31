package game.Server;

import game.Process.Bullet;
import game.Process.GameFrame;
import game.Process.GameMap;
import game.Process.GameState;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This class is a single user information keeper
 * to store and restore the users information.
 */
public class User implements Serializable {

    private String userName, password;
    private transient Socket clientSocket;
    private GameState state;
    private transient GameFrame canvas; // joone madaretoon be in frame dast nazanin
    private transient ArrayList<Bullet> bullets;
    private transient Vector<User> playersVector;
    private transient GameMap gameMap;

    /**
     * The main constructor of the User class.
     *
     * @param userName the username
     * @param password the password
     */
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;

    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return user.password.equals(password) &&
                user.userName.equals(userName);
    }

    /**
     * A getter method for getting the user name.
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * A getter method for getting the user password.
     *
     * @return the user password
     */
    public String getPassword() {
        return password;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void startTheGame() throws IOException {

        init();

        while (!state.gameOver) {
            write(state);
            bullets = (ArrayList<Bullet>) read();
            playersVector = (Vector<User>) read();
            canvas.setBullets(bullets);
            canvas.render(playersVector);
            state.update();
        }
    }

    private void init() throws IOException {
        clientSocket = new Socket("127.0.0.1", 2726);
        try {
            PrintStream out = new PrintStream(clientSocket.getOutputStream());
            out.println(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        gameMap = (GameMap) read();
        state = (GameState) read();
        playersVector = (Vector<User>) read();

        canvas = new GameFrame("Jtank", true);
        canvas.setLocationRelativeTo(null);
        canvas.setVisible(true);
        canvas.initBufferStrategy();
        canvas.setGameMap(gameMap);
        canvas.addKeyListener(state.getKeyListener());
        canvas.addMouseListener(state.getMouseListener());
        canvas.addMouseMotionListener(state.getMouseMotionListener());
        state.setLimits(canvas.getGameMap().getNumberOfRows(), canvas.getGameMap().getNumberOfColumns());
        state.width = canvas.getImage().getWidth() / 8; // Setting the width and the height
        canvas.setVisible(true);
    }

    public GameFrame getCanvas() {
        return canvas;
    }

    public void setCanvas(GameFrame canvas) {
        this.canvas = canvas;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(ArrayList<Bullet> bullets) {
        this.bullets = bullets;
    }

    private Object read() {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void write(Object object) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
