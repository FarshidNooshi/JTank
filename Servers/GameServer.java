package game.Servers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {

    private static int counter = 100;

    public static void main (String[] args) {

        ExecutorService service = Executors.newCachedThreadPool();

        try (ServerSocket welcomeSocket = new ServerSocket(2020)) {

        } catch (IOException e) {
            System.out.println("Error in starting server :: " + e.getMessage());
        }

    }

    private static class InGameClient implements Runnable {

        private Socket socket;

        public InGameClient (Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

        }
    }
}
