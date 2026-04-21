import java.util.*;

class Movie implements Comparable<Movie> {
    private String title;
    private String genre;
    private int popularity;
    private double price;

    public Movie(String title, String genre, int popularity, double price) {
        this.title = title;
        this.genre = genre;
        this.popularity = popularity;
        this.price = price;
    }

    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getPopularity() { return popularity; }
    public double getPrice() { return price; }

    @Override
    public int compareTo(Movie other) {
        if (this.popularity != other.popularity) {
            return Integer.compare(other.popularity, this.popularity);
        }
        return this.title.compareTo(other.title);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-25s | Price: $%.2f | Pop: %d", genre, title, price, popularity);
    }
}

class Box {
    private List<Movie> movies = new ArrayList<>();
    private List<Box> nestedBoxes = new ArrayList<>();

    public void addMovie(Movie m) { movies.add(m); }
    public void addBox(Box b) { nestedBoxes.add(b); }
    public List<Movie> getMovies() { return movies; }
    public List<Box> getNestedBoxes() { return nestedBoxes; }
}