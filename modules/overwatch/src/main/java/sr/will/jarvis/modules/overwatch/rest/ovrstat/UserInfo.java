package sr.will.jarvis.modules.overwatch.rest.ovrstat;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class UserInfo {
    public String icon;
    public String name;
    public int level;
    public String levelIcon;
    public int endorsement;
    public String endorsementIcon;
    public int prestige;
    public String prestigeIcon;
    public int rating;
    public String ratingIcon;
    public int gamesWon;
    public Stats quickPlayStats;
    public Stats competitiveStats;
    @SerializedName("private")
    public boolean private_;

    public String message;

    public String battletag;
    public String playOverwatchUrl;

    public class Stats {
        public HashMap<String, TopHero> topHeroes;
        public HashMap<String, CareerStat> careerStats;

        public class TopHero {
            public String name;
            public String timePlayed;
            public int timePlayedInSeconds;
            public int gamesWon;
            public int winPercentage;
            public int weaponAccuracy;
            public double eliminationsPerLife;
            public int multiKillBest;
            public int objectiveKills;

        }

        public class CareerStat {
            public HashMap<String, Integer> assists;
            public HashMap<String, Double> average;
            public HashMap<String, Object> best;
            public HashMap<String, Object> combat;
            public Game game;
            public HashMap<String, Integer> matchAwards;
            public HashMap<String, Integer> miscellaneous;

            public class Game {
                public int gamesLost;
                public int gamesPlayed;
                public int gamesTied;
                public int gamesWon;
                public String timePlayed;
            }
        }
    }
}
