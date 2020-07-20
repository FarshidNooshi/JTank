package game.Server;
//Copy&Paste from insomnia project
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * this class is for writing an object to a file
 */
public class Writer {
    private ObjectOutputStream out;

    public Writer(String path) throws IOException {
        out = new ObjectOutputStream(new FileOutputStream(new File(path)));
    }

    void WriteToFile(Object obj) throws IOException {
        out.writeObject(obj);
        out.close();
    }
}
