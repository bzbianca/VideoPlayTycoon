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
        StringBuilder bar = new StringBuilder("["); 
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
                System.out.println("EVELYN: \"Hey Manager! I am Evelyn, Miss Yao's bodyguard and manager. And I've been watching you from afar and Astra");
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
            System.out.println("\n[QUEST] Eous is buzzing around the shop with a dust cleaner.");
            if (getYesNo("Eous", "En-na-na? (I can help with the atmosphere of the store and take some stress off you!)")) {
                    reputation += 20; inv.organize();
                    if (energy < 100) { energy = Math.min(100, energy + 25);}
                    System.out.println(">> STATUS: Eous boosts morale! +25 Energy, +20 Rep.");
            }
        } else if (roll < 45) { 
            System.out.println("\n[QUEST] Nicole is leaning on the counter with a suspicious 'investment' flyer.");
            if (getYesNo("Nicole", "Manager! I've got a foolproof scheme. Give me $50, and I'll 'diversify' it into $120. Don't worry about the complaints!")) {
                if (balance >= 50) { balance += 70; reputation -= 15; System.out.println(">> STATUS: Nicole hands you a wad of cash. +$120 (Net +$70), -15 Rep."); }
            }
        } else if (roll < 75) { 
            System.out.println("\n[QUEST] Anby is staring at the burger shop across the street while holding a stack of flyers.");
            if (getYesNo("Anby", "Manager... I can go for a burger right now. If you get me my burger ($30), I will spread the word about the store when I go on my lunch break.")) {
                if (balance >= 30) { balance -= 30; reputation += 25; System.out.println(">> STATUS: Anby eats a burger and tells everyone she meets. +25 Rep, -$30."); }
            }
        }
    }

    private boolean getYesNo(String character, String question) {
        System.out.print("\n" + character + ": \"" + question + "\" (y/n): ");
        return sc.nextLine().trim().toLowerCase().equals("y");
    }

    private void playDay() {
        System.out.println("\n--- [ SHOP OPEN ] ---");
        for (int i = 0; i < 3; i++) {
            List<Movie> currentShelf = inv.getShelf();
            if (currentShelf.isEmpty()) break;
            
            showInventory();
            String name = characterNames[rnd.nextInt(characterNames.length)];
            
            String scenario;
            switch (name) {
                case "Anby": scenario = " is quietly humming while checking the expiration date on a burger wrapper. "; break;
                case "Nicole": scenario = " is aggressively calculating her debts on a gold-plated calculator. "; break;
                case "Billy": scenario = " is strike-posing and practicing his 'Starlight Knight' transformation. "; break;
                case "Anton": scenario = " is flexing his drill-arm while complaining about 'lack of spirit' in modern films. "; break;
                case "Ben": scenario = " is carefully adjusting his glasses and checking the store's architectural integrity. "; break;
                case "Koleda": scenario = " is standing on a crate so she can see over the counter properly. "; break;
                case "Grace": scenario = " is staring intensely at your Bangboo, wondering if she can take it apart. "; break;
                case "Lycaon": scenario = " is elegantly adjusting his tie and waiting with perfect posture. "; break;
                case "Ellen": scenario = " is leaning against the wall, looking like she’d rather be literally anywhere else. "; break;
                case "Rina": scenario = " is floating slightly off the ground, followed by two giggling dolls. "; break;
                case "Corin": scenario = " is clutching her chainsaw-bag and apologizing for 'taking up space'. "; break;
                case "Zhu Yuan": scenario = " is scanning the room for safety violations and taking official notes. "; break;
                case "Qingyi": scenario = " is spinning her staff and speaking in cryptic, old-fashioned metaphors. "; break;
                case "Jane": scenario = " is playing with a coin and giving you a look that makes you feel very nervous. "; break;
                case "Seth": scenario = " is standing at attention and trying very hard to look professional. "; break;
                case "Nekomata": scenario = " is perched on top of a shelf, swiping playfully at a dangling wire. "; break;
                case "Soldier 11": scenario = " is observing the store as if it were a high-stakes tactical battlefield. "; break;
                case "Soukaku": scenario = " is trying to see if any of the tapes are edible (they aren't). "; break;
                case "Lucy": scenario = " is being followed by three small boars wearing tiny baseball caps. "; break;
                case "Piper": scenario = " is yawning widely and looks like she might fall asleep mid-sentence. "; break;
                case "Miyabi": scenario = " is radiating an aura of cold authority that makes the room feel chilly. "; break;
                case "Harumasa": scenario = " is checking his reflection in a tape cover and fixing his hair. "; break;
                case "Yanagi": scenario = " is looking over the store's efficiency and nodding to herself. "; break;
                case "Lighter": scenario = " is shadow-boxing in the Action aisle with intense focus. "; break;
                case "Caesar": scenario = " is standing tall, looking like she’s ready to lead a parade. "; break;
                case "Burnice": scenario = " is looking at the posters and wondering if they'd look better with more fire. "; break;
                case "Nangong Yu": scenario = " is examining the shelf labels for any grammatical errors. "; break;
                case "Sunna": scenario = " is quietly observing the customers with a gentle smile. "; break;
                case "Aria": scenario = " is humming a melody that sounds like a classical opera. "; break;
                case "Zhao": scenario = " is standing stiffly, looking for anything out of the ordinary. "; break;
                case "Ye Shunguang": scenario = " is checking his watch and looking slightly impatient. "; break;
                case "Dialyn": scenario = " is taking a photo of a retro tape cover for her collection. "; break;
                case "Banyue": scenario = " is looking at the Family section with a nostalgic expression. "; break;
                case "Lucia": scenario = " is wearing a very fashionable coat and judging the store's decor. "; break;
                case "Manato": scenario = " is looking for a tape that will help him relax after a long shift. "; break;
                case "Yidhari": scenario = " is moving gracefully between the aisles like a shadow. "; break;
                case "Seed": scenario = " is standing very still, almost like a statue, in the corner. "; break;
                case "Orphie": scenario = " is looking through the Horror section with a brave face. "; break;
                case "Yuzuha": scenario = " is writing something down in a small, leather-bound notebook. "; break;
                case "Alice": scenario = " is spinning around in circles, looking very energetic. "; break;
                case "Pan Yinhu": scenario = " is examining the Action tapes with a critical eye. "; break;
                case "Yixuan": scenario = " is quietly browsing the Documentary section. "; break;
                case "Ju Fufu": scenario = " is holding a lucky charm and looking for a happy movie. "; break;
                case "Vivian": scenario = " is checking the lighting of the store for her next vlog. "; break;
                case "Hugo": scenario = " is looking for a tape that features fast cars. "; break;
                case "Pulchra": scenario = " is admiring the artistic design of the old retro covers. "; break;
                case "Trigger": scenario = " is checking the security cameras and nodding at you. "; break;
                default: scenario = " is browsing the aisles with an interested expression. "; break;
            }

            String goal = currentShelf.get(rnd.nextInt(currentShelf.size())).getGenre();
            
            String inquiry;
            switch (name) {
                case "Anby": inquiry = "\"Manager... do you have any " + goal + " tapes? I need something to watch during my burger break.\""; break;
                case "Nicole": inquiry = "\"Out of the way! I need a " + goal + " tape that I can resell—I mean, for personal study!\""; break;
                case "Billy": inquiry = "\"MANAGER! To truly understand a hero, I must study the finest " + goal + " tapes you have!\""; break;
                case "Anton": inquiry = "\"Hey! Show me where you keep the " + goal + " section. I need something with real SPIRIT!\""; break;
                case "Ben": inquiry = "\"Excuse me. I am looking for a " + goal + " film for our break room. Safety first, of course.\""; break;
                case "Koleda": inquiry = "\"Don't look down at me! Just show me the " + goal + " tapes already!\""; break;
                case "Grace": inquiry = "\"I wonder how the internal mechanism of a " + goal + " projector works... Show me a tape!\""; break;
                case "Lycaon": inquiry = "\"Good day. I require a " + goal + " masterpiece to maintain the household's refined atmosphere.\""; break;
                case "Ellen": inquiry = "\"...Whatever. Just give me a " + goal + " tape. I'm on my break.\""; break;
                case "Rina": inquiry = "\"Oh dear, my friends are bored. Do you have any lovely " + goal + " films for us?\""; break;
                case "Corin": inquiry = "\"I'm s-sorry! Please don't be mad! I just... I really wanted to see a " + goal + " movie!\""; break;
                case "Zhu Yuan": inquiry = "\"Official business. I need to review a " + goal + " tape for... case purposes. Yes, for the case.\""; break;
                case "Qingyi": inquiry = "\"Old souls often seek wisdom in the " + goal + " arts. Show me what has stood the test of time.\""; break;
                case "Jane": inquiry = "\"Care to play a game? Or perhaps just point me toward the " + goal + " tapes... if you can focus.\""; break;
                case "Seth": inquiry = "\"Reporting for duty! I'm here to purchase one " + goal + " tape for study, sir!\""; break;
                case "Nekomata": inquiry = "\"Is the " + goal + " section over here? I feel like watching something fast-paced!\""; break;
                case "Soldier 11": inquiry = "\"Identify the " + goal + " sector immediately. This is a high-priority mission.\""; break;
                case "Soukaku": inquiry = "\"If I watch a " + goal + " movie, will it make me less hungry? Actually, never mind, just give me one!\""; break;
                case "Lucy": inquiry = "\"Listen up, Manager! Bring me your most prestigious " + goal + " tape at once!\""; break;
                case "Piper": inquiry = "\"...Yawn... You got any of those " + goal + " things? I need something to do while the truck cools.\""; break;
                case "Miyabi": inquiry = "\"I need a " + goal + " tape. This is of course for my training. Training on " + goal + " tapes.\""; break;
                case "Harumasa": inquiry = "\"A stylish hero needs a stylish " + goal + " movie. What's your best one?\""; break;
                case "Yanagi": inquiry = "\"Efficiency is key. Show me your most popular " + goal + " tape so I don't waste time.\""; break;
                case "Lighter": inquiry = "\"I need a " + goal + " tape that's got some real punch to it! What've you got?\""; break;
                case "Caesar": inquiry = "\"A leader must understand all walks of life. Show me your " + goal + " collection!\""; break;
                case "Burnice": inquiry = "\"Do you have any " + goal + " movies with big explosions? I love the heat!\""; break;
                case "Nangong Yu": inquiry = "\"I require a " + goal + " film for research. Please ensure it is the uncut version.\""; break;
                case "Sunna": inquiry = "\"Could you help me find a " + goal + " movie that's peaceful to watch?\""; break;
                case "Aria": inquiry = "\"The rhythm of a " + goal + " story is so unique. Do you have any recommendations?\""; break;
                case "Zhao": inquiry = "\"I'm on a mission for a " + goal + " tape. Point the way.\""; break;
                case "Ye Shunguang": inquiry = "\"I'm in a hurry. Just give me your best " + goal + " tape and I'll be on my way.\""; break;
                case "Dialyn": inquiry = "\"I'm looking for a " + goal + " tape with a really cool cover. What've you got?\""; break;
                case "Banyue": inquiry = "\"I'd like to see a " + goal + " movie tonight. It's been a while.\""; break;
                case "Lucia": inquiry = "\"I only watch the most elegant " + goal + " films. I hope you have something suitable.\""; break;
                case "Manato": inquiry = "\"I need a good " + goal + " movie to decompress. Any suggestions?\""; break;
                case "Yidhari": inquiry = "\"A " + goal + " story sounds perfect for a quiet night. Show me where they are.\""; break;
                case "Seed": inquiry = "\"... " + goal + " tape. Please.\""; break;
                case "Orphie": inquiry = "\"I want to try watching a " + goal + " movie. I hope it's not too scary!\""; break;
                case "Yuzuha": inquiry = "\"I'm looking for a " + goal + " tape to study for my next project.\""; break;
                case "Alice": inquiry = "\"Do you have anything in " + goal + "? I want something fun to watch!\""; break;
                case "Pan Yinhu": inquiry = "\"I need a " + goal + " tape that actually has a good plot. Don't disappoint me.\""; break;
                case "Yixuan": inquiry = "\"I'm interested in the " + goal + " genre. What do you have in stock?\""; break;
                case "Ju Fufu": inquiry = "\"Will a " + goal + " movie bring me good luck? I'll take one anyway!\""; break;
                case "Vivian": inquiry = "\"I need a " + goal + " tape that looks good on screen. What's your most aesthetic one?\""; break;
                case "Hugo": inquiry = "\"Got any " + goal + " movies with fast-paced action? I'm ready!\""; break;
                case "Pulchra": inquiry = "\"I love the art style of " + goal + " films. Which one do you recommend?\""; break;
                case "Trigger": inquiry = "\"I'm checking your " + goal + " section for quality control. I'll take this one.\""; break;
                default: inquiry = "\"I'm looking for something in the " + goal + " genre. What do you recommend?\""; break;
            }

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
        System.out.println("\n" + "=".repeat(60));
        if (status.equals("VICTORY")) {
            System.out.println("   + ASTRA YAO'S ENDORSEMENT +");
            System.out.println("ASTRA YAO: \"I've seen many shops, but this one has soul.");
            System.out.println("Your curation is art, Manager. Well done!\"");
        } else {
            System.out.println("          --- THE SHOP HAS FAILED ---");
            System.out.println("ASTRA YAO: \"I expected more from you. It looks like you really don't care about my concerts..\"");
        }
        System.out.println("=".repeat(60));

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
        System.out.println();
        System.out.println("=".repeat(60));
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