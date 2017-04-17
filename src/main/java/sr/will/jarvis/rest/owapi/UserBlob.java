package sr.will.jarvis.rest.owapi;

import java.util.HashMap;

public class UserBlob {
    public Request _request;
    public Region any;
    public Region eu;
    public Region kr;
    public Region us;
    public String msg;
    public String error;

    public class Request {
        public String api_ver;
        public String route;
    }

    public class Region {
        public Achievements achievements;
        public Heroes heroes;
        public Stats stats;

        public class Achievements {
            public HashMap<String, Boolean> defense;
            public HashMap<String, Boolean> general;
            public HashMap<String, Boolean> maps;
            public HashMap<String, Boolean> offense;
            public HashMap<String, Boolean> special;
            public HashMap<String, Boolean> support;
            public HashMap<String, Boolean> tank;
        }

        public class Heroes {
            public Playtime playtime;
            public Stats stats;

            public class Playtime {
                public HashMap<String, Double> competitive;
                public HashMap<String, Double> quickplay;
            }

            public class Stats {
                public HashMap<String, Hero> competitive;
                public HashMap<String, Hero> quickplay;

                public class Hero {
                    public HashMap<String, Double> average_stats;
                    // Double currently fails because of a bug with owapi
                    public HashMap<String, Object> general_stats;
                    public HashMap<String, Double> hero_stats;
                }
            }
        }

        public class Stats {
            public Mode competitive;
            public Mode quickplay;

            public class Mode {
                public HashMap<String, Double> average_stats;
                public boolean competitive;
                public HashMap<String, Double> game_stats;
                public OverallStats overall_stats;

                public class OverallStats {
                    public String avatar;
                    public int comprank;
                    public int games;
                    public int level;
                    public int losses;
                    public int prestige;
                    public String rank_image;
                    public String tier;
                    public double win_rate;
                    public int wins;
                }
            }
        }
    }

    public Region getRegion() {
        if (us != null) {
            return us;
        } else if (eu != null) {
            return eu;
        } else if (kr != null) {
            return kr;
        }

        return null;
    }
}
