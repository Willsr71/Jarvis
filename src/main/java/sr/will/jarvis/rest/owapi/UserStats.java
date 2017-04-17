package sr.will.jarvis.rest.owapi;

public class UserStats {
    public Region us;
    public Region eu;
    public Region kr;
    public Region any;
    public String msg;
    public String error;

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

    public class Region {
        public Stats stats;

        public class Stats {
            public Mode competitive;
            public Mode quickplay;

            public class Mode {
                public OverallStats overall_stats;
                public GameStats game_stats;
                public boolean competitive;
                public AverageStats average_stats;

                public class OverallStats {
                    public double win_rate;
                    public int level;
                    public int prestige;
                    public String avatar;
                    public int wins;
                    public int games;
                    public int comprank;
                    public int losses;
                }

                public class GameStats {
                    public double objective_kills;
                    public int games_won;
                    public double kpd;
                    public int objecive_kills_most_in_game;
                    public double time_spent_on_fire_most_in_game;
                    public int healing_done;
                    public int defensive_assists;
                    public int offensive_assists;
                    public int final_blows_most_in_game;
                    public double objetive_time;
                    public int melee_final_blows;
                    public int medals;
                    public int cards;
                    public int multikill_best;
                    //public int overwatch.guid.0x086000000000042e;
                    public int multikills;
                    public int defensive_assists_most_in_game;
                    public int offensive_assists_most_in_game;
                    public int melee_final_blow_most_in_game;
                    public int damage_done;
                    public int medals_silver;
                    public int medals_gold;
                    public int medals_bronze;
                    public int healing_done_most_in_game;
                    public int environmental_kills;
                    public int solo_kills;
                    public double time_spent_on_fire;
                    public int eliminations_most_in_game;
                    public int final_blows;
                    public double time_played;
                    public int environmental_deaths;
                    public int solo_kills_most_in_game;
                    public int damage_done_most_in_game;
                    public int games_played;
                    public int eliminations;
                    public double obective_time_most_in_game;
                    public int deaths;
                }

                public class AverageStats {
                    public double healing_done_avg;
                    public double eliminations_avg;
                    public double melee_final_blows_avg;
                    public double final_blows_avg;
                    public double defensive_assists_avg;
                    public double damage_done_avg;
                    public double deaths_avg;
                    public double objective_time_avg;
                    public double offensive_assists_avg;
                    public double solo_kills_avg;
                    public double time_spent_on_fire_avg;
                    public double objective_kills_avg;
                }
            }
        }
    }
}
