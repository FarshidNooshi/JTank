package game.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * This class is for reading an object(request) from a path.
 * In this class we read the users information from a saved file
 * to check them for logging in and signing up.
 */
public class Reader {

    private ObjectInputStream in;

    /**
     * The main constructor of the Reader class
     * to get a path and read the data from file.
     *
     * @param path the file path
     * @throws IOException not finding file exception
     */
    Reader(String path) throws IOException {
        in = new ObjectInputStream(new FileInputStream(new File(path)));
    }

    /**
     * This method will read the list of the old users
     * and wll return them back.
     *
     * @return the list
     * @throws IOException not finding class exception
     */
    Object ReadFromFile() throws IOException {
        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Closing the opened file.
     */
    public void close() {
        try {
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
