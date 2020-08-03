package game.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

public class TankHandler extends Thread {

    private ServerSocket serverSocket;
    public boolean serverOn = true;

    public TankHandler () {
        try {
            serverSocket = new ServerSocket(1725);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (serverOn) {
            try {
                Socket socket = serverSocket.accept();
                Scanner scanner = new Scanner(socket.getInputStream());
                String userName = scanner.nextLine();
                String imagePath = scanner.nextLine();
                String bulletPath = scanner.nextLine();
                String path = new URI("src/game/Server/info.aut").getPath();
                ArrayList<User> arrayList = (ArrayList<User>) new Reader(path).ReadFromFile();
                System.out.println(arrayList.size());
                for (User user : arrayList)
                    if (user.getUserName().equals(userName)) {
                        user.imagePath = imagePath;
                        user.bulletPath = bulletPath;
                    }
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
