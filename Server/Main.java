package game.Server;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {// TODO: 21-Jul-20 bayad ghesmate entezar baraye bazi dorost beshe
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        int counter = 0;
        try (ServerSocket welcomingSocket = new ServerSocket(1726)) {
            JOptionPane.showMessageDialog(null, "Server started.");
            while (counter++ < 100000) {
                Socket connectionSocket = welcomingSocket.accept();
                System.out.println("client accepted!");
                pool.execute(new ClientHandler(connectionSocket, counter));
            }
            pool.shutdown();
            JOptionPane.showMessageDialog(null, "closing server.");
        } catch (IOException ex) {
            System.err.println(ex);
        }
        System.out.println("done.");
    }

}

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
            OutputStream out = connectionSocket.getOutputStream();
            InputStream in = connectionSocket.getInputStream();
            {
                byte[] buffer = new byte[2048];
                int read = in.read();
                String tmp = String.valueOf(in.read(buffer, 0, read));
                if (tmp.equals("Log in")) {
                    String userName = String.valueOf(in.read(buffer, 0, in.read()));
                    String password = String.valueOf(in.read(buffer, 0, in.read()));
                    if (check(userName, password)) {
                        // TODO: 21-Jul-20 authenticated user.
                    } else {

                    }
                } else if (tmp.equals("Sign in")) {
                    String userName = String.valueOf(in.read(buffer, 0, in.read()));
                    String password = String.valueOf(in.read(buffer, 0, in.read()));
                    if (!find(userName)) {
                        User userToCreate = new User(userName, password);
                        try {
                            String path = new URI("src/game/Server/info.aut").getPath();
                            ArrayList<User> arrayList;
                            arrayList = (ArrayList<User>) new Reader(path).ReadFromFile();
                            Writer writer = new Writer(path);
                            arrayList.add(userToCreate);
                            writer.WriteToFile(arrayList);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // TODO: 21-Jul-20 join user in
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
                System.err.println(ex);
            }
        }
    }

    private boolean find(String userName) {
        try {
            String path = new URI("src/game/Server/info.aut").getPath();
            ArrayList<User> arrayList;
            arrayList = (ArrayList<User>) new Reader(path).ReadFromFile();
            for (User user : arrayList)
                if (user.getUserName().equals(userName))
                    return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean check(String id, String pass) {
        try {
            String path = new URI("src/game/Server/info.aut").getPath();
            ArrayList<User> arrayList;
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


