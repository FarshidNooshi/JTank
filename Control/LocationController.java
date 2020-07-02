package game.Control;

import game.Process.GameState;

import java.util.ArrayList;

public class LocationController {

    private static ArrayList<Location> locations;
    private static boolean created = false;

    public static void init(GameState gameState) {
        locations = new ArrayList<>();
        created = true;
    }

    public static void add (Location location) {
        if (created)
            locations.add(location);
    }

    public static void remove (int x, int y) {

        if (!created)
            return;

        for (Location l : locations)
            if (l.isMatch(x, y)) {
                locations.remove(l);
                break;
            }

    }

    public static boolean check (int x, int y) {

        if (!created)
            return true;

        for (Location l : locations)
            if( l.isOverlap(x, y) )
                return false;

        return true;
    }
}
