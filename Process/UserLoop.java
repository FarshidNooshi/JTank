package game.Process;

import game.Server.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class UserLoop extends Thread{

    private User thisPlayerUser;
    private GameFrame canvas;
    private Vector<User> users;
    private Socket clientSocket;

    public UserLoop(User user) {

        thisPlayerUser = user;

        try {
            user.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        clientSocket = user.getClientSocket();

        canvas = new GameFrame("Jtank");
        canvas.setGameMap(user.getGameMap());
        canvas.setVisible(true);
        canvas.initBufferStrategy();
        user.getState().setLimits(canvas.getGameMap().getNumberOfRows(), canvas.getGameMap().getNumberOfColumns());
        user.getState().width = canvas.getImage().getWidth() / 8; // Setting the width and the height
        user.getState().height = canvas.getImage().getHeight() / 8;
    }

    @Override
    public void run() {
        while (!thisPlayerUser.getState().gameOver) {
            thisPlayerUser.getState().update();
            write(thisPlayerUser.getState());
            canvas.setBullets((ArrayList<Bullet>) read());
            users = (Vector<User>) read();
            for (User u : users)
                if (u.getUserName().equals(thisPlayerUser.getUserName()))
                    thisPlayerUser.setState(u.getState());
            canvas.addKeyListener(thisPlayerUser.getState().getKeyListener());
            canvas.addMouseListener(thisPlayerUser.getState().getMouseListener());
            canvas.addMouseMotionListener(thisPlayerUser.getState().getMouseMotionListener());
            canvas.render(users);
        }
    }

    public Object read() {
        try {
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            return in.readUnshared();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(Object object) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeUnshared(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
