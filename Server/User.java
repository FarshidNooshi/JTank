package game.Server;

import game.Process.GameMap;
import game.Process.GameState;

import java.io.*;
import java.net.Socket;

/**
 * This class is a single user information keeper
 * to store and restore the users information.
 */
public class User implements Serializable {
    // Private fields
    private String userName, password;
    public String imagePath, bulletPath; // I think you might need them zeus
    private transient GameState state;
    private transient GameMap gameMap;
    private transient Socket clientSocket; // This socket is different in server
    public transient ObjectOutputStream out; // and the input and output streams
    public transient ObjectInputStream in; // are different in server side and client side.
    public DataBox dataBox;

    /**
     * The main constructor of the User class.
     *
     * @param userName the username
     * @param password the password
     */
    public User(String userName, String password) {
        //
        this.userName = userName;
        this.password = password;
        //
        dataBox = new DataBox(); // We just use data box to send the data we need
        dataBox.userName = userName;
    }

    /**
     * This method will get the user ready for the
     * game.
     *
     * @throws IOException write into stream exception
     */
    public void init() throws IOException {
        // Creating the user socket
        // init is called in UserLoop constructor
        clientSocket = new Socket("127.0.0.1", 2726); // The other hand sets in game handler
        try {
            PrintStream stream = new PrintStream(clientSocket.getOutputStream()); // Sending the user name
            stream.println(userName);
            out = new ObjectOutputStream(clientSocket.getOutputStream()); // Opening the streams
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will set the data box data
     * up to date.
     */
    public void updateDataBox() {
        //
        dataBox.locX = state.locX;
        dataBox.locY = state.locY;
        //
        dataBox.width = state.width;
        dataBox.height = state.height;
        //
        dataBox.direction = state.direction();
        dataBox.gameOver = state.gameOver;
    }

    // Setters and getters
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public Object read() {
        try {
            return in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(Object object) {
        try {
            out.writeObject(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
