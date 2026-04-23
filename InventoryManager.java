import java.util.*;

/**
 * Manages store shelves and cleanliness levels.
 */
public class InventoryManager {
    private final List<Movie> shelf = new ArrayList<>();
    private int disorderLevel = 0;

    /**
     * Recursive method to stock movies from a box.
     */
    public void recursiveStock(Box box) {
        if (box == null || box.isEmpty()) return;
        shelf.add(box.pullMovie());
        recursiveStock(box);
    }

    public void organize() { disorderLevel = 0; }
    public void incrementDisorder() { disorderLevel += 4; }
    public int getDisorderLevel() { return disorderLevel; }
    public List<Movie> getShelf() { return shelf; }
    public void sellMovie(Movie m) { shelf.remove(m); }
}