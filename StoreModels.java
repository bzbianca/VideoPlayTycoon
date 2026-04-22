import java.util.*;

class Movie implements Comparable<Movie> {
    private String title;
    private String genre;
    private int popularity;
    private double price;

    public Movie(String title, String genre, int popularity) {
        this.title = title;
        this.genre = genre;
        this.popularity = popularity;

        // Dynamic Pricing
        if (popularity > 90) this.price = 85.0;
        else if (popularity > 70) this.price = 60.0;
        else if (popularity > 40) this.price = 35.0;
        else this.price = 15.0;
    }

    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public double getPrice() { return price; }

    @Override
    public int compareTo(Movie other) {
        return Integer.compare(other.popularity, this.popularity);
    }

    @Override
    public String toString() {
        // Corrected padding for menu alignment
        return String.format("%-12s | %-25s ... $%-6.2f (Pop: %d)",
                "[" + genre + "]", title, price, popularity);
    }
}