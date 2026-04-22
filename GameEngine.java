import java.util.*;

public class GameEngine {
    private InventoryManager inv = new InventoryManager();
    private double balance = 500.0;
    private double totalProfit = 0;
    private int reputation = 60;
    private int energy = 100;
    private int day = 1;
    private int totalSales = 0;
    private Scanner sc = new Scanner(System.in);
    private Random rnd = new Random();

    private String[] characterNames = {"Anby", "Nicole", "Billy", "Anton", "Ben", "Koleda", "Grace", "Ellen", "Zhu Yuan", "Lycaon", "Rina", "Corin"};
    private String[][] tapeData = {
            {"Starlight Knight", "Action"}, {"The Heartbeat", "Romance"}, {"Nihility", "Retro"},
            {"The Big Hollow", "Suspense"}, {"Free Solo", "Documentary"}, {"Return to Joy", "Family"},
            {"Justice", "Action"}, {"White Night", "Suspense"}, {"Iron-Hoof Knight", "Action"},
            {"Bizarre Love Story", "Romance"}, {"Hollow Zero", "Documentary"}, {"New World", "Sci-Fi"},
            {"The Last City", "Urban"}, {"Small Body Big Crisis", "Comedy"}, {"Ghost Train", "Horror"}
    };

    public static void main(String[] args) { new GameEngine().run(); }

    private String getEnergyBar() {
        int bars = energy / 10;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) bar.append((i < bars) ? "█" : "░");
        return bar.toString() + "] " + energy + "/100";
    }

    public void run() {
        showInstructions();
        String endReason = "Closed Business Early";

        while (reputation > 0 && balance > 0 && reputation < 500) {
            if (energy <= 10) {
                forceRest();
                day++;
                continue;
            }

            int choice = preOpenPhase();
            if (choice == 5) break;
            if (choice == 3) { day++; continue; }
            if (choice == 6) { openAuction(); day++; continue; }

            triggerSideQuest();
            playDay();
            day++;
        }

        if (reputation >= 500) endReason = "VICTORY! You are the Video Tycoon of the City!";
        else if (reputation <= 0) endReason = "DEFEAT: Your reputation was destroyed.";
        else if (balance <= 0) endReason = "DEFEAT: You ran out of cash.";

        showResults(endReason);
    }

    private void showInstructions() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                VIDEO STORE TYCOON: MASTER OPERATING MANUAL");
        System.out.println("=".repeat(70));
        System.out.println("1. OBJECTIVE: Reach 500 Reputation to win the game.");
        System.out.println("\n2. THE ENERGY SYSTEM:");
        System.out.println("   - Everything costs Energy. If your bar hits 10, you collapse.");
        System.out.println("   - Open Shop (-30), Stock (-15), Organize (-20), Auction (-10/15).");
        System.out.println("   - Rest (+60) skips the day but recovers your strength.");
        System.out.println("\n3. SALES & REPUTATION:");
        System.out.println("   - Match the requested genre to gain +5 Rep and the Tape's Value.");
        System.out.println("   - High Reputation = Higher chance for Tips (Price + Tip).");
        System.out.println("   - Wrong genres or skipping customers results in -10 Rep.");
        System.out.println("\n4. THE BLACK MARKET:");
        System.out.println("   - Spend the full day here instead of opening the shop.");
        System.out.println("   - Trade Reputation for rare tapes or sell stock for DOUBLE value.");
        System.out.println("\n5. STORE MAINTENANCE:");
        System.out.println("   - Stocking adds 'Mess'. If Mess > 5, you lose Rep daily.");
        System.out.println("   - Use the 'Organize' action to reset your Mess level.");
        System.out.println("=".repeat(70));
        System.out.println("Press Enter to open your shop doors...");
        sc.nextLine();
    }

    private void forceRest() {
        System.out.println("\n[!] CRITICAL EXHAUSTION: You collapsed! Energy reset to 100.");
        energy = 100;
        reputation -= 5;
    }

    private int preOpenPhase() {
        while (true) {
            System.out.println("\n" + "=".repeat(55));
            System.out.printf(" DAY %-3d | CASH: $%-7.2f | REP: %-3d/500\n", day, balance, reputation);
            System.out.println(" ENERGY: " + getEnergyBar());
            System.out.println("=".repeat(55));
            System.out.println("1) Buy Shipment ($75)     [-15 Energy]");
            System.out.println("2) Organize Shelves       [-20 Energy]");
            System.out.println("3) Rest for the Day       [+60 Energy]");
            System.out.println("4) Open Shop              [-30 Energy]");
            System.out.println("5) Exit Game");
            System.out.println("6) BLACK MARKET AUCTION   [Whole Day]");
            System.out.print("\nCommand Selection > ");

            String choice = sc.nextLine();
            if (choice.equals("1") && balance >= 75) {
                balance -= 75; energy -= 15;
                Box b = new Box();
                for(int i=0; i<3; i++) {
                    String[] t = tapeData[rnd.nextInt(tapeData.length)];
                    b.addMovie(new Movie(t[0], t[1], rnd.nextInt(100) + 1));
                }
                inv.recursiveStock(b);
                System.out.println(">> Shipment unpacked! Tapes are on the shelves.");
            } else if (choice.equals("2")) {
                inv.organize(); energy -= 20;
                System.out.println(">> Store organized. The shelves are gleaming.");
            } else if (choice.equals("3")) {
                energy = Math.min(100, energy + 60);
                System.out.println(">> You took a much-needed nap.");
                return 3;
            } else if (choice.equals("4")) {
                if (inv.getShelf().isEmpty()) {
                    System.out.println("!! Error: No stock! Buy a shipment before opening.");
                    continue;
                }
                energy -= 30; return 4;
            } else if (choice.equals("5")) return 5;
            else if (choice.equals("6")) return 6;
        }
    }

    private void openAuction() {
        System.out.println("\n" + "~".repeat(50));
        System.out.println("             BLACK MARKET DISTRICT");
        System.out.printf(" REP: %-3d | ENERGY: %s\n", reputation, getEnergyBar());
        System.out.println("~".repeat(50));
        System.out.println("1) BUY: Trade 25 Reputation for a High-Value Tape [-10 Energy]");
        System.out.println("2) SELL: Select a tape to sell for DOUBLE Value   [-15 Energy]");
        System.out.println("3) Leave District");
        System.out.print("\nBlack Market Command > ");

        String sub = sc.nextLine();
        if (sub.equals("1") && reputation > 25 && energy >= 10) {
            reputation -= 25; energy -= 10;
            String[] t = tapeData[rnd.nextInt(tapeData.length)];
            inv.getShelf().add(new Movie(t[0], t[1], 98));
            System.out.println(">> You bartered your reputation for " + t[0] + ".");
        } else if (sub.equals("2") && !inv.getShelf().isEmpty() && energy >= 15) {
            System.out.println("\nSelect a tape from your inventory to sell:");
            for (int i = 0; i < inv.getShelf().size(); i++) {
                System.out.println((i+1) + ". " + inv.getShelf().get(i));
            }
            try {
                int sel = Integer.parseInt(sc.nextLine()) - 1;
                Movie m = inv.getShelf().remove(sel);
                double payout = m.getPrice() * 2;
                balance += payout; totalProfit += payout; energy -= 15;
                System.out.printf(">> Shady Dealer purchased %s for $%.2f!\n", m.getTitle(), payout);
            } catch (Exception e) { System.out.println(">> Transaction failed."); }
        } else {
            System.out.println(">> You left the market empty-handed.");
        }
    }

    private void triggerSideQuest() {
        if (rnd.nextInt(10) < 8) {
            int ev = rnd.nextInt(2);
            if (ev == 0) {
                if (getYesNo("Eous", "En-na! (I can reorganize while you sleep for $15?)")) {
                    if (balance >= 15) { balance -= 15; inv.organize(); }
                }
            } else {
                if (getYesNo("Nicole", "I need to borrow your storefront for a video. $50 for the trouble?")) {
                    balance += 50; reputation += 5;
                }
            }
        }
    }

    private boolean getYesNo(String character, String question) {
        while (true) {
            System.out.println("\n" + character + ": \"" + question + "\"");
            System.out.print("(y/n): ");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
            System.out.println(">> " + character + " stares blankly. 'I didn't get that. Yes or no?'");
        }
    }

    private void playDay() {
        System.out.println("\n--- STORE OPEN: BUSY HOURS ---");
        for (int i = 0; i < 3; i++) {
            if (inv.getShelf().isEmpty()) break;
            String name = characterNames[rnd.nextInt(characterNames.length)];
            String goal = inv.getShelf().get(rnd.nextInt(inv.getShelf().size())).getGenre();

            System.out.println("\n" + ".".repeat(60));
            System.out.println("CURRENT INVENTORY:");
            for (int j = 0; j < inv.getShelf().size(); j++) System.out.println((j + 1) + ". " + inv.getShelf().get(j));
            System.out.println(".".repeat(60));
            System.out.println(name + ": \"Hey! Looking for a " + goal + " video tape.\"");
            System.out.print("Select tape # to recommend (0 to skip) > ");

            try {
                int sel = Integer.parseInt(sc.nextLine()) - 1;
                if (sel >= 0) {
                    Movie m = inv.getShelf().get(sel);
                    if (m.getGenre().equalsIgnoreCase(goal)) {
                        double price = m.getPrice();
                        double tip = (rnd.nextInt(100) < reputation) ? 15 + rnd.nextInt(15) : 0;
                        balance += (price + tip); totalProfit += (price + tip);
                        totalSales++; reputation += 5;
                        inv.sellMovie(m);
                        if (tip > 0) System.out.printf(">> Sold! $%.2f + $%.2f tip\n", price, tip);
                        else System.out.printf(">> Sold for $%.2f\n", price);
                    } else { reputation -= 10; System.out.println(">> " + name + ": 'That's not what I asked for!'"); }
                }
            } catch (Exception e) { System.out.println(">> Customer lost interest."); }
        }
        inv.incrementDisorder();
    }

    private void showResults(String reason) {
        System.out.println("\n" + "#".repeat(45));
        System.out.println("           FINAL STORE REPORT");
        System.out.println("#".repeat(45));
        System.out.println(" STATUS:        " + reason);
        System.out.println(" DAYS OPEN:     " + (day-1));
        System.out.println(" TAPES SOLD:    " + totalSales);
        System.out.printf(" FINAL CASH:    $%.2f\n", balance);
        System.out.printf(" TOTAL PROFIT:  $%.2f\n", totalProfit);
        System.out.println(" FINAL REP:     " + reputation + "/500");
        System.out.println("#".repeat(45));
    }
}