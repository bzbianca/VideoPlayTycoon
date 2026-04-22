import java.util.*;

public class InventoryManager {
    private List<Movie> shelf = new ArrayList<>();
    private int disorderLevel = 0;

    public int recursiveStock(Box box) {
        int count = 0;
        for (Movie m : box.getMovies()) { shelf.add(m); count++; }
        for (Box b : box.getNestedBoxes()) { count += recursiveStock(b); }
        disorderLevel += 2;
        return count;
    }

    public void organize() { disorderLevel = 0; }
    public int getDisorderLevel() { return disorderLevel; }
    public void incrementDisorder() { disorderLevel++; }
    public List<Movie> getShelf() { return shelf; }
    public void sellMovie(Movie m) { shelf.remove(m); }
}