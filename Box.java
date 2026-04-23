import java.util.*;

/**
 * Represents a shipment container.
 */
public class Box {
    private List<Movie> contents = new ArrayList<>();

    public void addMovie(Movie m) { contents.add(m); }

    public Movie pullMovie() {
        if (contents.isEmpty()) return null;
        return contents.remove(0);
    }

    public boolean isEmpty() { return contents.isEmpty(); }
}