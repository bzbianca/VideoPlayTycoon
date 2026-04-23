import java.util.*;

/**
 * The main driver class for Video Store Tycoon.
 */
public class GameEngine {
    private final InventoryManager inv = new InventoryManager();
    private double balance = 500.0;
    private double totalRevenue = 0;
    private double totalSpent = 75.0; 
    private int reputation = 50;
    private final int initialRep = 50;
    private int energy = 100;
    private int day = 1;
    private final int MAX_DAYS = 31;
    private int actionsLeft = 5; 
    private int totalSales = 0;
    
    private final Scanner sc = new Scanner(System.in);
    private final Random rnd = new Random();

    private final String[] characterNames = {
        "Anby", "Nicole", "Billy", "Anton", "Ben", "Koleda", "Grace", "Ellen", 
        "Zhu Yuan", "Lycaon", "Rina", "Corin", "Qingyi", "Seth", "Jane", 
        "Nekomata", "Soldier 11", "Soukaku", "Lucy", "Piper", "Nangong Yu", 
        "Sunna", "Aria", "Zhao", "Ye Shunguang", "Dialyn", "Banyue", "Lucia", 
        "Manato", "Yidhari", "Seed", "Orphie", "Yuzuha", "Alice", "Pan Yinhu", 
        "Yixuan", "Ju Fufu", "Vivian", "Hugo", "Pulchra", "Trigger", "Miyabi", 
        "Harumasa", "Yanagi", "Lighter", "Caesar", "Burnice"
    };

    private final String[][] tapeData = {
            {"Starlight Knight", "Action"}, {"The Heartbeat", "Romance"}, {"Nihility", "Retro"},
            {"The Big Hollow", "Suspense"}, {"Free Solo", "Documentary"}, {"Return to Joy", "Family"},
            {"Justice", "Action"}, {"White Night", "Suspense"}, {"Iron-Hoof Knight", "Action"},
            {"Bizarre Love Story", "Romance"}, {"Hollow Zero", "Documentary"}, {"New World", "Sci-Fi"},
            {"The Last City", "Urban"}, {"Small Body Big Crisis", "Comedy"}, {"Ghost Train", "Horror"}
    };

    public static void main(String[] args) { new GameEngine().run(); }

    private String getEnergyBar() {
        int bars = Math.max(0, energy / 10);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) bar.append((i < bars) ? "█" : "░");
        return bar.toString() + "] " + energy + "/100";
    }

    private String getMessBar() {
        int val = inv.getDisorderLevel();
        int bars = Math.min(val / 2, 10);
        StringBuilder bar = new StringBuilder("["); // Fixed compilation error here
        for (int i = 0; i < 10; i++) bar.append((i < bars) ? "!" : ".");
        return bar.toString() + "] " + val + "/20";
    }

    public void run() {
        showInstructions();
        String endStatus = "QUIT";
        boolean evelynArriving = false;

        while (reputation > 0 && balance > 0 && reputation < 500 && day <= MAX_DAYS) {
            
            if (evelynArriving) {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("!!! UNEXPECTED VISITOR AT THE FRONT DOOR !!!");
                System.out.println("EVELYN: \"Manager, I've been watching you from afar and Astra");
                System.out.println("said that you deserve the early endorsement of coming to");
                System.out.println("the concert afterall! You've done incredible work here.\"");
                System.out.println("=".repeat(60));
                System.out.println("Press Enter to follow Evelyn to the concert...");
                sc.nextLine();
                reputation = 500; 
                break;
            }

            actionsLeft = 5; 
            int choice = preOpenPhase();
            if (choice == 7) break; 
            
            if (choice == 5) {
                triggerSideQuest();
                playDay();
            }
            
            if (balance > 500 && reputation >= 400) {
                evelynArriving = true;
            }

            if (choice != 7 && choice != 6) day++; 
        }

        if (reputation >= 500) endStatus = "VICTORY";
        else if (day > MAX_DAYS) endStatus = "OUT_OF_TIME";
        else if (reputation <= 0) endStatus = "REP_LOSS";
        else if (balance <= 0) endStatus = "BANKRUPT";

        showResults(endStatus);
    }

    private void showInstructions() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                VIDEO STORE TYCOON: THE ASTRA YAO CHALLENGE");
        System.out.println("=".repeat(80));
        System.out.println("MANAGER'S OPERATING MECHANICS:");
        System.out.println("- GOAL: Reach 500 Reputation within 31 DAYS to impress Astra Yao.");
        System.out.println("- ENERGY: Opening shop costs 30, Organizing costs 20, Shipments cost 15.");
        System.out.println("- BLACK MARKET: A full-day commitment. You can barter Rep for rare tapes.");
        System.out.println("- DANGER: Hitting 0 Energy in the Black Market loses 20 Rep and 2 Days.");
        System.out.println("- SALES: You must match the genre. High Rep increases your chance for tips.");
        System.out.println("- DAILY LIMIT: You have 5 prep slots per day for buying or cleaning.");
        System.out.println("- MESSINESS: Messy shops lower tips. Use the 'Organize' command to clean.");
        System.out.println("- INVENTORY: Check your stock anytime. Tapes will be listed during sales.");
        System.out.println("=".repeat(80));
        System.out.println("Astra Yao's concert is in one month. Don't let her down.");
        System.out.println("Press Enter to open the doors for Day 1...");
        sc.nextLine();
    }

    private int preOpenPhase() {
        while (true) {
            // Auto-exhaustion and Prep Slot reset logic
            if (energy <= 0 || actionsLeft <= 0) {
                System.out.println("\n>> STATUS CHANGE: +20 energy due to exhaustion");
                energy = Math.min(100, Math.max(0, energy) + 20);
                return 4; 
            }

            System.out.println("\n" + "=".repeat(55));
            System.out.printf(" DAY %d/%d | CASH: $%.2f | REP: %d/500\n", day, MAX_DAYS, balance, reputation);
            System.out.println(" ENERGY:    " + getEnergyBar());
            System.out.println(" MESSINESS: " + getMessBar());
            System.out.println(" PREP SLOTS: " + actionsLeft + "/5");
            System.out.println("=".repeat(55));
            System.out.println("1) Buy Shipment ($75)     [-15 Energy]");
            System.out.println("2) Check Store Inventory");
            System.out.println("3) Organize Shelves       [-20 Energy]");
            System.out.println("4) Rest for the Day       [+60 Energy]");
            System.out.println("5) Open Shop              [-30 Energy]");
            System.out.println("6) BLACK MARKET AUCTION   [Whole Day]");
            System.out.println("7) Exit Game");
            System.out.print("\nCommand Selection > ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    if (balance >= 75) {
                        balance -= 75; totalSpent += 75; energy -= 15; actionsLeft--;
                        inv.recursiveStock(generateBox());
                        inv.incrementDisorder(); 
                        System.out.println(">> STATUS: Shipment received.");
                    } else {
                        System.out.println(">> Insufficient cash.");
                    }
                    break;
                case "2":
                    showInventory();
                    actionsLeft--;
                    break;
                case "3":
                    inv.organize();
                    energy -= 20; actionsLeft--;
                    System.out.println(">> STATUS: Shelves are organized.");
                    break;
                case "4":
                    energy = Math.min(100, energy + 60);
                    return 4;
                case "5":
                    if (energy < 30) {
                        System.out.println("!! Not enough energy to open shop.");
                        energy = 0; 
                    } else {
                        energy -= 30; return 5;
                    }
                    break;
                case "6":
                    openAuction();
                    return 6;
                case "7": return 7;
                default: continue;
            }
        }
    }

    private void openAuction() {
        boolean inAuction = true;
        while (inAuction) {
            System.out.println("\n--- BLACK MARKET DISTRICT ---");
            System.out.printf(" REP: %d | CASH: $%.2f | ENERGY: %s\n", reputation, balance, getEnergyBar());
            System.out.println("1) BUY: Valuable Tape [-15 energy, -25 rep]");
            System.out.println("2) SELL: Tape for DOUBLE Value [-20 energy]");
            System.out.println("3) LEAVE DISTRICT (Ends Day)");

            System.out.print("\nCommand Selection > ");
            String sub = sc.nextLine();

            if (sub.equals("1")) {
                if (reputation >= 25 && energy >= 15) {
                    reputation -= 25; energy -= 15;
                    Movie masterpiece = new Movie("The Forbidden Cut", "Retro", 100);
                    inv.getShelf().add(masterpiece);
                    System.out.println(">> STATUS: Acquired " + masterpiece.getTitle() + ".");
                } else {
                    System.out.println(">> STATUS: Insufficient stats.");
                }
            } else if (sub.equals("2")) {
                if (!inv.getShelf().isEmpty() && energy >= 20) {
                    showInventory();
                    System.out.print("Select Tape # to Fence > ");
                    try {
                        int sel = Integer.parseInt(sc.nextLine()) - 1;
                        Movie m = inv.getShelf().remove(sel);
                        double payout = m.getPrice() * 2;
                        balance += payout; totalRevenue += payout; energy -= 20;
                        System.out.println(">> STATUS: Fenced " + m.getTitle() + " for $" + payout + ".");
                    } catch (Exception e) { System.out.println(">> STATUS: Transaction failed."); }
                } else {
                    System.out.println(">> STATUS: Cannot fence at this time.");
                }
            } else if (sub.equals("3")) {
                inAuction = false;
            }

            if (energy <= 0) {
                System.out.println("\n!!! BLACK MARKET COLLAPSE !!!");
                reputation -= 20; day += 2; energy = 100; inAuction = false;
            }
        }
    }

    private void triggerSideQuest() {
        int roll = rnd.nextInt(100);
        if (roll < 15) { 
            System.out.println("\n[QUEST] Eous is buzzing around the shop with a holographic projector.");
            if (getYesNo("Eous", "En-na-na? (I can help with the atmosphere and take some stress off you for $40?)")) {
                if (balance >= 40) {
                    balance -= 40; energy = Math.min(100, energy + 25);
                    reputation += 20; inv.organize();
                    System.out.println(">> STATUS: Eous boosts morale! +25 Energy, +20 Rep, Mess Cleared.");
                }
            }
        } else if (roll < 45) { 
            System.out.println("\n[QUEST] Nicole is leaning on the counter with a suspicious 'investment' flyer.");
            if (getYesNo("Nicole", "Manager! I've got a foolproof scheme. Give me $50, and I'll 'diversify' it into $120. Don't worry about the complaints!")) {
                if (balance >= 50) { balance += 70; reputation -= 15; System.out.println(">> STATUS: Nicole hands you a wad of cash. +$120 (Net +$70), -15 Rep."); }
            }
        } else if (roll < 75) { 
            System.out.println("\n[QUEST] Anby is staring at the burger shop across the street while holding a stack of flyers.");
            if (getYesNo("Anby", "Manager... my blood-sugar levels are dropping. If you fund my burger lunch ($30), I will spread the word about this store during my meal.")) {
                if (balance >= 30) { balance -= 30; reputation += 25; System.out.println(">> STATUS: Anby eats a burger and tells everyone she meets. +25 Rep, -$30."); }
            }
        }
    }

    private boolean getYesNo(String character, String question) {
        System.out.print("\n" + character + ": \"" + question + "\" (y/n): ");
        return sc.nextLine().trim().toLowerCase().equals("y");
    }

    private void playDay() {
        String[] customerScenarios = {
            " is a tired salaryman rubbing his eyes. ",
            " is a student with a notebook. ",
            " looks like they're going on a first date. ",
            " is a parent with two chaotic kids. ",
            " is an old collector in a trench coat. ",
            " is a local biker looking for thrills. "
        };

        String[] inquiryStyles = {
            "\"I need to forget today happened. What do you got in the %s genre?\"",
            "\"I'm doing a project on cinematic history! Do you have any %s tapes?\"",
            "\"I need something that isn't awkward. Help me find a %s film?\"",
            "\"Give me something that will keep them quiet. Got any %s movies?\"",
            "\"I haven't seen this title in thirty years... Show me your %s selection.\"",
            "\"Something with loud engines and explosions! What's the best %s you have?\"",
            "\"I'm in the mood for something specific. Where is the %s section?\"",
            "\"Manager, recommend me your finest %s tape.\""
        };

        System.out.println("\n--- [ SHOP OPEN ] ---");
        for (int i = 0; i < 3; i++) {
            List<Movie> currentShelf = inv.getShelf();
            if (currentShelf.isEmpty()) break;
            
            showInventory();
            String name = characterNames[rnd.nextInt(characterNames.length)];
            String scenario = customerScenarios[rnd.nextInt(customerScenarios.length)];
            String goal = currentShelf.get(rnd.nextInt(currentShelf.size())).getGenre();
            String inquiry = String.format(inquiryStyles[rnd.nextInt(inquiryStyles.length)], goal);

            System.out.println("\n" + name + scenario);
            System.out.println(name + ": " + inquiry);
            System.out.print("Select Tape # > ");

            try {
                int sel = Integer.parseInt(sc.nextLine()) - 1;
                Movie m = currentShelf.get(sel);
                if (m.getGenre().equalsIgnoreCase(goal)) {
                    double messPenalty = (inv.getDisorderLevel() > 10) ? -15 : 0;
                    double tip = 0;
                    if (rnd.nextInt(600) < (reputation + 50)) {
                        tip = 10 + rnd.nextInt(21); 
                    }
                    tip = Math.max(0, tip + messPenalty);
                    double total = m.getPrice() + tip;
                    balance += total; totalRevenue += total; reputation += 5; totalSales++;
                    inv.sellMovie(m);
                    
                    if (tip > 0) {
                        System.out.printf(">> STATUS CHANGE: +$%.2f + $%.2f tip | +5 Rep\n", m.getPrice(), tip);
                    } else {
                        System.out.printf(">> STATUS CHANGE: +$%.2f | +5 Rep\n", total);
                    }
                } else { 
                    reputation -= 10; System.out.println(">> STATUS CHANGE: -10 Rep"); 
                }
            } catch (Exception e) { System.out.println(">> Customer lost interest and left."); }
        }
        inv.incrementDisorder();
    }

    private void showResults(String status) {
        System.out.println("\n" + "★ ".repeat(15));
        if (status.equals("VICTORY")) {
            System.out.println("   ✨ ASTRA YAO'S PLATINUM ENDORSEMENT ✨");
            System.out.println("ASTRA YAO: \"I've seen many shops, but this one has soul.");
            System.out.println("Your curation is art, Manager. Well done!\"");
        } else {
            System.out.println("          --- THE SHOP HAS FAILED ---");
            System.out.println("ASTRA YAO: \"I expected a professional. This was a mess.\"");
        }
        System.out.println("★ ".repeat(15));

        System.out.println("\n--- [ FINAL PERFORMANCE REPORT ] ---");
        System.out.printf("  STATUS:           %s\n", status.replace("_", " "));
        System.out.printf("  OPERATIONAL DAYS: %d / %d\n", (day - 1), MAX_DAYS);
        System.out.printf("  TOTAL SALES:      %d\n", totalSales);
        System.out.println("-".repeat(40));
        System.out.printf("  GROSS REVENUE:    $%.2f\n", totalRevenue);
        System.out.printf("  TOTAL EXPENSES:   $%.2f\n", totalSpent);
        System.out.printf("  NET PROFIT:       $%.2f\n", (totalRevenue - totalSpent));
        System.out.println("-".repeat(40));
        System.out.printf("  STARTING REP:     %d\n", initialRep);
        System.out.printf("  FINAL REP:        %d / 500\n", reputation);
        System.out.printf("  REP CHANGE:       %+d\n", (reputation - initialRep));
        System.out.println("-".repeat(40));
        System.out.printf("Days Survived: %d | Final Rep: %d | Net Profit: $%.2f\n", day-1, reputation, (totalRevenue - totalSpent));
        System.out.println("#".repeat(60));
    }

    private Box generateBox() {
        Box b = new Box();
        for(int i=0; i<3; i++) {
            String[] t = tapeData[rnd.nextInt(tapeData.length)];
            b.addMovie(new Movie(t[0], t[1], rnd.nextInt(100) + 1));
        }
        return b;
    }

    private void showInventory() {
        System.out.println("\n--- [ CURRENT STORE STOCK ] ---");
        List<Movie> shelf = inv.getShelf();
        if (shelf.isEmpty()) System.out.println("Empty.");
        for (int i = 0; i < shelf.size(); i++) {
            System.out.println((i + 1) + ". " + shelf.get(i));
        }
    }
}