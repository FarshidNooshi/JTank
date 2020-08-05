package game.Server;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private static HashMap<Integer, CopyOnWriteArrayList<User>> queue = new HashMap<Integer, CopyOnWriteArrayList<User>>();
    //
    public static int gamePort = 2000;
    public static ArrayList<GameData> data = new ArrayList<>();
    //

    public static void main(String[] args) {
        try {
            clearData();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        int counter = 0;
        try (ServerSocket welcomingSocket = new ServerSocket(1726)) {
            JOptionPane.showMessageDialog(null, "Server started.");
            while (counter++ < 1000) {
                // Waiting for a client and connect them to the server
                Socket connectionSocket = welcomingSocket.accept();
                ClientHandler clientHandler = new ClientHandler(connectionSocket);
                pool.execute(clientHandler);
            }
            pool.shutdown();
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
        writer.writeToFile(arrayList);
        path = new URI("src/game/Server/remember.aut").getPath(); // File path of users
        ArrayList<Pair<String, User>> arrayList2 = new ArrayList<>();
        writer = new Writer(path); // Save all the users again
        writer.writeToFile(arrayList2);
    }

    public static HashMap<Integer, CopyOnWriteArrayList<User>> getQueue() {
        return queue;
    }

    public static void setQueue(HashMap<Integer, CopyOnWriteArrayList<User>> queue) {
        Main.queue = queue;
    }

    public static ExecutorService getService() {
        return service;
    }

    public static void setService(ExecutorService service) {
        Main.service = service;
    }
}