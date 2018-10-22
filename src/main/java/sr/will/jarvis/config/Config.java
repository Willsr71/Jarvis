package sr.will.jarvis.config;

import java.util.ArrayList;

public class Config {
    public Discord discord = new Discord();
    public Sql sql = new Sql();
    public Stats stats = new Stats();
    public Cache cache = new Cache();
    public String serverUUID = "";
    public boolean debug = false;

    public class Discord {
        public String token = "";
        public ArrayList<String> owners = new ArrayList<>();
        public long statusMessageInterval = 60;
        public ArrayList<String> statusMessages = new ArrayList<>();
        public ArrayList<String> pinEmotes = new ArrayList<>();
    }

    public class Sql {
        public String host = "localhost";
        public String database = "jarvis";
        public String user = "jarvis";
        public String password = "password";
    }

    public class Stats {
        public boolean enabled = false;
        public int interval = 1;
        public String host = "localhost";
        public int port = 8125;
        public String prefix = "jarvis";
    }

    public class Cache {
        public int muteCacheTimeout = 300;
        public int muteCacheCleanupInterval = 10;
        public int moduleCacheTimeout = 600;
        public int moduleCacheCleanupInterval = 60;
    }
}
