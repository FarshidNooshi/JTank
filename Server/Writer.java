package game.Server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is for writing an object to a file.
 * In this class we create a file and will save the server
 * data in it.
 */
public class Writer {

    private ObjectOutputStream out;

    /**
     * The main constructor of the Writer class.
     *
     * @param path the file path to store data
     * @throws IOException file not found exception
     */
    Writer(String path) throws IOException {
        out = new ObjectOutputStream(new FileOutputStream(new File(path)));
    }

    /**
     * A method for closing the opened file.
     *
     * @param obj the list to store
     * @throws IOException class not found exception
     */
    void WriteToFile(Object obj) throws IOException {
        if (obj instanceof ArrayList) {
            ArrayList<User> users = (ArrayList<User>) obj;
            for (User i : users)
                out.writeObject(i);
        }
        out.close();
    }
}
