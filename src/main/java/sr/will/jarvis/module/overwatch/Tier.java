package sr.will.jarvis.module.overwatch;

public enum Tier {
    BRONZE(0, "Bronze", "https://blzgdapipro-a.akamaihd.net/game/rank-icons/season-2/rank-1.png"),
    SILVER(1500, "Silver", "https://blzgdapipro-a.akamaihd.net/game/rank-icons/season-2/rank-2.png"),
    GOLD(2000, "Gold", "https://blzgdapipro-a.akamaihd.net/game/rank-icons/season-2/rank-3.png"),
    PLATINUM(2500, "Platinum", "https://blzgdapipro-a.akamaihd.net/game/rank-icons/season-2/rank-4.png"),
    DIAMOND(3000, "Diamond", "https://blzgdapipro-a.akamaihd.net/game/rank-icons/season-2/rank-5.png"),
    MASTERS(2500, "Masters", "https://blzgdapipro-a.akamaihd.net/game/rank-icons/season-2/rank-6.png"),
    GRANDMASTERS(3000, "Grandmasters", "https://blzgdapipro-a.akamaihd.net/game/rank-icons/season-2/rank-7.png");

    private int minSR;
    private String name;
    private String imageURL;

    Tier(int minSR, String name, String imageURL) {
        this.minSR = minSR;
        this.name = name;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public int getMinSR() {
        return minSR;
    }

    public String getImageURL() {
        return imageURL;
    }

    public static Tier fromSR(int sr) {
        // This is quite possibly the most cancerous function in this entire codebase.
        // If anyone has suggestions they are welcome
        // as I am entirely too lazy to figure out a better method at the moment

        if (sr >= GRANDMASTERS.minSR) {
            return GRANDMASTERS;
        } else if (sr >= MASTERS.minSR) {
            return MASTERS;
        } else if (sr >= DIAMOND.minSR) {
            return DIAMOND;
        } else if (sr >= PLATINUM.minSR) {
            return PLATINUM;
        } else if (sr >= GOLD.minSR) {
            return GOLD;
        } else if (sr >= SILVER.minSR) {
            return SILVER;
        } else {
            return BRONZE;
        }
    }
}
