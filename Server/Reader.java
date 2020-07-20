package game.Server;
// Copy&Paste from insomnia project
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * this class is for reading an object(request) from a path
 */
public class Reader {
    private ObjectInputStream in;

    public Reader(String path) throws IOException {
        in = new ObjectInputStream(new FileInputStream(new File(path)));
    }

    public Object ReadFromFile() throws IOException {
        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
