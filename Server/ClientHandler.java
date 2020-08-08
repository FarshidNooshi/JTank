package game.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This is the in-server handler to get the client socket
 * and connect the client to a game so that the user
 * can play.
 */
public class ClientHandler implements Runnable {

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
                    if (check(userName, password)) {
                        out.println("user entered the game.");
                        //
                        tankServer();
                        //
                        int result = gameChooser();
                        if (result == 1)
                            return;
                        //
                        String name = in.nextLine();
                        int numberOfPlayers = Integer.parseInt(in.nextLine());
                        String mathType = in.nextLine();
                        int bulletSpeed = Integer.parseInt(in.nextLine());
                        int tankSpeed = Integer.parseInt(in.nextLine());
                        int wallHealth = Integer.parseInt(in.nextLine());
                        int tankHealth = Integer.parseInt(in.nextLine());
                        int rounds = Integer.parseInt(in.nextLine());
                        String type = in.nextLine();
                        String play = in.nextLine();
                        //
                        GameData gameData = new GameData();
                        gameData.name = name;
                        gameData.matchType = mathType;
                        gameData.numberOfPeople = numberOfPlayers;
                        gameData.bulletSpeed = bulletSpeed;
                        gameData.tankSpeed = tankSpeed;
                        gameData.wallHealth = wallHealth;
                        gameData.tankHealth = tankHealth;
                        gameData.numberOfRounds = rounds;
                        gameData.ip = "127.0.0.1";
                        gameData.port = Main.gamePort;
                        if (!type.equals("Single"))
                            gameData.isTeamBattle = true;
                        gameData.gamePlay = play;
                        //
                        client.gameData = gameData;
                        try (ObjectOutputStream out2 = new ObjectOutputStream(connectionSocket.getOutputStream())) {
                            out2.writeObject(client);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //
                        CopyOnWriteArrayList<User> temp = new CopyOnWriteArrayList<>();
                        temp.add(client);
                        Main.data.add(gameData);
                        Main.getQueue().put(gameData.port, temp);
                        Main.gamePort++;
                        //
                        Main.getService().execute(new GameHandler(temp, gameData, numberOfPlayers));
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
                            writer.writeToFile(arrayList);
                            out.println("accepted");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        out.println("not accepted, user name exist.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tankServer() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(1725);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert serverSocket != null;
            Socket socket = serverSocket.accept();
            Scanner scanner = new Scanner(socket.getInputStream());
            String s1 = scanner.nextLine();
            String s2 = scanner.nextLine();
            client.setImagePath(s1);
            client.setBulletPath(s2);
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int gameChooser() {
        try (ServerSocket serverSocket = new ServerSocket(1724)) {
            //
            Socket socket = serverSocket.accept();
            //
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(Main.data);
            //
            int result = socket.getInputStream().read();
            if (result == 1)
                connectToGame();
            //
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void connectToGame() {
        try (ServerSocket serverSocket = new ServerSocket(1723)) {
            Socket socket = serverSocket.accept();
            //
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(client);
            //
            int port = new DataInputStream(socket.getInputStream()).readInt();
            //
            for (Integer i : Main.getQueue().keySet())
                if (i == port) {
                    Main.getQueue().get(i).add(client);
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
