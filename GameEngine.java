import java.util.*;

public class GameEngine {
    private InventoryManager inv = new InventoryManager();
    private Storage<Double> balance = new Storage<>(300.0);
    private int reputation = 60;
    private int day = 1;
    private Scanner sc = new Scanner(System.in);
    private Random rnd = new Random();

    // Data sourced from ZZZ Videotape List
    private String[][] zzzTapes = {
        {"Starlight Knight", "Action"}, {"The Heartbeat", "Romance"}, 
        {"Small Body Big Crisis", "Comedy"}, {"Nihility", "Retro"},
        {"The Big Hollow", "Suspense"}, {"Free Solo", "Documentary"},
        {"Return to Joy", "Family"}, {"Justice", "Action"},
        {"The Last City", "Urban"}, {"White Night", "Suspense"},
        {"Iron-Hoof Knight", "Action"}, {"Bizarre Love Story", "Romance"},
        {"Hollow Zero", "Documentary"}, {"New World", "Sci-Fi"}
    };

    public static void main(String[] args) {
        new GameEngine().run();
    }

    public void run() {
        System.out.println("=== RANDOM PLAY: VIDEO TYCOON ===");
        while (reputation > 0 && balance.get() > 0) {
            preOpenPhase();
            playDay();
            day++;
        }
        System.out.println("\n--- GAME OVER ---");
        System.out.println("Your store lasted " + (day-1) + " days.");
        if (reputation <= 0) System.out.println("Reason: Poor Reputation.");
        if (balance.get() <= 0) System.out.println("Reason: Bankruptcy.");
    }

    private void preOpenPhase() {
        int attempts = 3;
        while (attempts > 0) {
            System.out.println("\n" + "=".repeat(30));
            System.out.println("       DAY " + day + " - PREPARATION");
            System.out.println("=".repeat(30));
            System.out.printf("Cash: $%.2f | Rep: %d | Actions: %d\n", balance.get(), reputation, attempts);
            System.out.println("1) Buy Stock Shipment ($100)");
            System.out.println("2) Organize Store (Refresh Popularity)");
            System.out.println("3) Finish Preparation");
            
            String choice = sc.nextLine();
            if (choice.equals("1")) {
                buyShipment();
                attempts--;
            } else if (choice.equals("2")) {
                inv.organize();
                System.out.println("Shelves organized! The TreeSet has been re-sorted.");
                attempts--;
            } else if (choice.equals("3")) {
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private void buyShipment() {
        if (balance.get() < 100) {
            System.out.println("Not enough money for stock!");
            return;
        }
        balance.set(balance.get() - 100);
        
        Box mainBox = new Box();
        // Adding a nested box for recursion demo
        Box innerBox = new Box();
        
        for (int i = 0; i < 2; i++) {
            String[] data = zzzTapes[rnd.nextInt(zzzTapes.length)];
            mainBox.addMovie(new Movie(data[0], data[1], rnd.nextInt(100), 25.0));
        }
        
        String[] innerData = zzzTapes[rnd.nextInt(zzzTapes.length)];
        innerBox.addMovie(new Movie(innerData[0], innerData[1], rnd.nextInt(100), 30.0));
        mainBox.addBox(innerBox);
        
        inv.recursiveStock(mainBox);
        System.out.println("Recursive stocking complete!");
    }

    private void playDay() {
        System.out.println("\n--- STORE IS OPEN FOR BUSINESS ---");
        
        if (inv.getShelf().isEmpty()) {
            System.out.println("No movies to sell! You lose reputation.");
            reputation -= 15;
            return;
        }

        for (int i = 0; i < 3; i++) {
            // Customer asks for a genre currently in stock
            Movie hint = inv.getShelf().get(rnd.nextInt(inv.getShelf().size()));
            String goalGenre = hint.getGenre();
            
            System.out.println("\nCustomer: 'Do you have any " + goalGenre + " tapes?'");
            System.out.println("Current Inventory:");
            for (int j = 0; j < inv.getShelf().size(); j++) {
                System.out.println((j+1) + ". " + inv.getShelf().get(j));
            }
            
            System.out.print("Recommend a tape (#) or 0 to skip: ");
            try {
                int selection = Integer.parseInt(sc.nextLine()) - 1;
                if (selection == -1) {
                    System.out.println("Customer left.");
                    reputation -= 5;
                } else {
                    Movie chosen = inv.getShelf().get(selection);
                    if (chosen.getGenre().equalsIgnoreCase(goalGenre)) {
                        System.out.println("Customer bought " + chosen.getTitle() + "!");
                        balance.set(balance.get() + chosen.getPrice());
                        reputation += 10;
                        inv.sellMovie(chosen);
                    } else {
                        System.out.println("Customer: 'This isn't what I wanted...'");
                        reputation -= 10;
                    }
                }
            } catch (Exception e) {
                System.out.println("Customer got confused and left.");
            }
            
            if (inv.getShelf().isEmpty()) {
                System.out.println("Shelves are now empty.");
                break;
            }
        }
    }
}