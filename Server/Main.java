package game.Server;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the main server part where we connect the user
 * to our main server and will let the user to login or sign up.
 * Then based on the game that the user choose we will connect them
 * to another port.
 */
public class Main {

    private static ExecutorService service = Executors.newCachedThreadPool();
    private static ExecutorService pool = Executors.newCachedThreadPool();
    private static HashMap<Integer, Vector<User>> queue = new HashMap<>();

    public static void main(String[] args) {
        try {
            clearData();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        TankHandler tankHandler = new TankHandler(); // to handle the users tanks choose
        tankHandler.start(); // is a single thread
        // The users ID
        int counter = 0;
        try (ServerSocket welcomingSocket = new ServerSocket(1726)) {
            JOptionPane.showMessageDialog(null, "Server started.");
            while (counter++ < 1000) {
                // Waiting for a client and connect them to the server
                Socket connectionSocket = welcomingSocket.accept();
                System.out.println("client accepted!");
                pool.execute(new ClientHandler(connectionSocket));
            }
            pool.shutdown();
            tankHandler.serverOn = false;
            JOptionPane.showMessageDialog(null, "closing server.");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        System.out.println("done.");
    }

    private static void clearData() throws URISyntaxException, IOException {
        String path = new URI("src/game/Server/info.aut").getPath(); // File path of users
        ArrayList<User> arrayList = new ArrayList<>();
        Writer writer = new Writer(path); // Save all the users again
        writer.WriteToFile(arrayList);
        path = new URI("src/game/Server/remember.aut").getPath(); // File path of users
        ArrayList<Pair<String, User>> arrayList2 = new ArrayList<>();
        writer = new Writer(path); // Save all the users again
        writer.WriteToFile(arrayList2);
    }

    public static HashMap<Integer, Vector<User>> getQueue() {
        return queue;
    }

    public static void setQueue(HashMap<Integer, Vector<User>> queue) {
        Main.queue = queue;
    }

    public static ExecutorService getService() {
        return service;
    }

    public static void setService(ExecutorService service) {
        Main.service = service;
    }
}

/**
 * This is the in-server handler to get the client socket
 * and connect the client to a game so that the user
 * can play.
 */
class ClientHandler implements Runnable {

    private Socket connectionSocket;
    private User client;

    ClientHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
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
                    String remember = in.nextLine();
                    if (check(userName, password) && remember.equalsIgnoreCase("remember")) {
                        try {
                            String path = new URI("src/game/Server/remember.aut").getPath(); // File path of users
                            ArrayList<Pair<String, User>> arrayList;
                            //noinspection unchecked
                            arrayList = (ArrayList<Pair<String, User>>) new Reader(path).ReadFromFile(); // Reading the old users info
                            //noinspection SuspiciousMethodCalls
                            if (!arrayList.contains(new Pair<>(connectionSocket.getInetAddress(), new User(userName, password)))) {
                                Writer writer = new Writer(path); // Save all the users again
                                arrayList.add(new Pair<>(connectionSocket.getInetAddress().toString(), new User(userName, password)));
                                writer.WriteToFile(arrayList);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (check(userName, password)) {
                        out.println("user entered the game.");
                        int numberOfPlayers = Integer.parseInt(in.nextLine());
                        Main.getQueue().putIfAbsent(numberOfPlayers, new Vector<>());
                        HashMap<Integer, Vector<User>> queue = Main.getQueue();
                        queue.get(numberOfPlayers).add(client);
                        try (ObjectOutputStream out2 = new ObjectOutputStream(connectionSocket.getOutputStream())) {
                            out2.writeObject(client);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (queue.get(numberOfPlayers).size() == numberOfPlayers) {
                            Vector<User> temp = new Vector<>(queue.get(numberOfPlayers));
                            queue.remove(numberOfPlayers);
                            Main.getService().execute(new GameHandler(temp));
                        }
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks if the username
     * is been used before or not. If the name is not unique
     * then it will not accept the new username.
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

    /**
     * This method is for logging into the server.
     * This will check the equality of the username and
     * the password of the user.
     */
    private boolean check(String id, String pass) {
        try {
            String path = new URI("src/game/Server/remember.aut").getPath(); // File path of users
            ArrayList<Pair<String, User>> arrayList;
            //noinspection unchecked
            arrayList = (ArrayList<Pair<String, User>>) new Reader(path).ReadFromFile(); // Reading the old users info
            for (Pair<String, User> stringUserPair : arrayList)
                if (connectionSocket.getInetAddress().toString().equals(stringUserPair.getFirst())) {
                    client = stringUserPair.getSecond();
                    client.setClientSocket(connectionSocket);
                    return true;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String path = new URI("src/game/Server/info.aut").getPath();
            ArrayList<User> arrayList;
            //noinspection unchecked
            arrayList = (ArrayList<User>) new Reader(path).ReadFromFile();
            for (User user : arrayList)
                if (user.equals(new User(id, pass))) {
                    client = new User(id, pass);
                    client.setClientSocket(connectionSocket);
                    return true;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}