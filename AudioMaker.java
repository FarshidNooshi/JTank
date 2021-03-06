package game;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AudioMaker {

    public static MakeEffect getSound(int type) {
        return new MakeEffect(type);
    }

    private static Thread mainTheme;

    static void startTheTheme() {
        mainTheme = new Thread(new StartTheme());
        mainTheme.start();
    }

    static void stop() {
        //noinspection deprecation
        mainTheme.stop();
    }

    static class MakeEffect implements Runnable {
        int type;
        MakeEffect(int type) {
            this.type = type;
        }
        @Override
        public void run() {
            try {
                FileInputStream file;
                String explodeFile = "src/game/Audio/explode.mp3";
                String fireFile = "src/game/Audio/Fire.mp3";
                if (type == 1)
                    file = new FileInputStream(new File(fireFile));
                else
                    file = new FileInputStream(new File(explodeFile));
                Player player = new Player(file);
                player.play();
            } catch (FileNotFoundException | JavaLayerException e) {
                e.printStackTrace();
            }
        }
    }

    static class StartTheme implements Runnable {
        @Override
        public void run() {
            try {
                FileInputStream file = new FileInputStream(new File("src/game/Audio/GameTheme.mp3"));
                Player player = new Player(file);
                player.play();
            } catch (FileNotFoundException | JavaLayerException e) {
                e.printStackTrace();
            }
        }
    }
}
