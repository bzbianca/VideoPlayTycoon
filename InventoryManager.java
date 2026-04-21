import java.util.*;

// Generics for safe storage
class Storage<T> {
    private T value;
    public Storage(T val) { this.value = val; }
    public T get() { return value; }
    public void set(T val) { this.value = val; }
}

public class InventoryManager {
    private List<Movie> shelf = new ArrayList<>();
    private TreeSet<Movie> popularHits = new TreeSet<>();

    // RECURSION: Unpacking boxes from shipments
    public void recursiveStock(Box box) {
        for (Movie m : box.getMovies()) {
            shelf.add(m);
            popularHits.add(m);
        }
        for (Box b : box.getNestedBoxes()) {
            recursiveStock(b);
        }
    }

    public List<Movie> getShelf() { return shelf; }
    public TreeSet<Movie> getPopularHits() { return popularHits; }
    
    public void sellMovie(Movie m) {
        shelf.remove(m);
        popularHits.remove(m);
    }
    
    public void organize() {
        List<Movie> temp = new ArrayList<>(shelf);
        popularHits.clear();
        popularHits.addAll(temp);
    }
}