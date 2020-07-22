package game.Server;

import javax.swing.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the main server part where we connect the user
 * to our main server and will let the user to login or sign up.
 * Then based on the game that the user choose we will connect them
 * to another port.
 */
public class Main {

    // TODO: 21-Jul-20 bayad ghesmate entezar baraye bazi dorost beshe

    public static void main(String[] args) {
        try {
            clearData();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        // MultiClient main server
        ExecutorService pool = Executors.newCachedThreadPool();
        // Number of the users in the server
        // The users ID
        int counter = 0;
        try (ServerSocket welcomingSocket = new ServerSocket(1726)) {
            JOptionPane.showMessageDialog(null, "Server started.");
            while (counter++ < 100000) {
                // Waiting for a client and connect them to the server
                Socket connectionSocket = welcomingSocket.accept();
                System.out.println("client accepted!");
                pool.execute(new ClientHandler(connectionSocket, counter));
            }
            pool.shutdown();
            JOptionPane.showMessageDialog(null, "closing server.");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        System.out.println("done.");
    }

    public static void clearData() throws URISyntaxException, IOException {
        String path = new URI("src/game/Server/info.aut").getPath(); // File path of users
        ArrayList<User> arrayList = new ArrayList<>();
        Writer writer = new Writer(path); // Save all the users again
        writer.WriteToFile(arrayList);
    }
}

/**
    This is the in-server handler to get the client socket
    and connect the client to a game so that the user
    can play.
 */
class ClientHandler implements Runnable {

    private Socket connectionSocket;
    private int clientNum;

    ClientHandler(Socket connectionSocket, int clientNum) {
        this.connectionSocket = connectionSocket;
        this.clientNum = clientNum;
    }

    @Override
    public void run() {
        try {
            PrintStream out = new PrintStream(connectionSocket.getOutputStream());
            Scanner in = new Scanner(connectionSocket.getInputStream());
            {
                String tmp = in.nextLine();
                if (tmp.equals("Log in")) {
                    String userName = in.nextLine(); // Checking the users
                    String password = in.nextLine(); // username and password
                    if (check(userName, password)) {
                        out.println("user entered the game.");
                        // TODO: 21-Jul-20 authenticated user.
                    } else {
                        out.println("user not found.");
                    }
                } else if (tmp.equalsIgnoreCase("Sign up")) {
                    String userName = in.nextLine(); // Checking the users
                    String password = in.nextLine(); // username and password
                    // check for non repeated names
                    if (!find(userName)) {
                        User userToCreate = new User(userName, password);
                        try {
                            String path = new URI("src/game/Server/info.aut").getPath(); // File path of users
                            ArrayList<User> arrayList;
                            //noinspection unchecked
                            arrayList = (ArrayList<User>) new Reader(path).ReadFromFile(); // Reading the old users info
                            Writer writer = new Writer(path); // Save all the users again
                            arrayList.add(userToCreate);
                            writer.WriteToFile(arrayList);
                            out.println("accepted");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        out.println("not accepted, user name exist.");
                    }
                } else {
                    // TODO: 21-Jul-20  gamePlay should be implemented here.
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                connectionSocket.close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    /*
        This method checks if the username
        is been used before or not. If the name is not unique
        then it will not accept the new username.
     */
    private boolean find(String userName) {
        try {
            String path = new URI("src/game/Server/info.aut").getPath();
            //noinspection unchecked
            ArrayList<User> arrayList = (ArrayList<User>) new Reader(path).ReadFromFile();
            System.out.println(arrayList.size());
            for (User user : arrayList)
                if (user.getUserName().equals(userName))
                    return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
        This method is for logging into the server.
        This will check the equality of the username and
        the password of the user.
     */
    private boolean check(String id, String pass) {
        try {
            String path = new URI("src/game/Server/info.aut").getPath();
            ArrayList<User> arrayList;
            //noinspection unchecked
            arrayList = (ArrayList<User>) new Reader(path).ReadFromFile();
            for (User user : arrayList)
                if (user.equals(new User(id, pass)))
                    return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}


