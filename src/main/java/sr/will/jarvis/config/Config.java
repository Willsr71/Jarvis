package sr.will.jarvis.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Config {
    public Discord discord = new Discord();
    public Sql sql = new Sql();
    public Stats stats = new Stats();
    public Cache cache = new Cache();
    public boolean debug = false;

    public class Discord {
        public String token = "TOKEN";
        public ArrayList<String> owners = new ArrayList<>(Collections.singletonList("112587845968912384"));
        public long statusMessageInterval = 60;
        public ArrayList<String> statusMessages = new ArrayList<>(Arrays.asList(
                "SyntaxError",
                "NullPointerException",
                "InterruptedException",
                "401 Unauthorized",
                "403 Forbidden",
                "404 Not Found",
                "406 Unacceptable",
                "410 Gone",
                "418 I'm a teapot",
                "503 Servers on Fire"
        ));
        public ArrayList<String> pinEmotes = new ArrayList<>(Arrays.asList(
                "\uD83D\uDC4C",
                "\uD83D\uDCCC",
                "\uD83D\uDCCD"
        ));
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
        public int cleanupInterval = 2;
        public Timeouts timeouts = new Timeouts();

        public class Timeouts {
            public int mute = 300;
            public int module = 600;
            public int userXp = 300;
            public int levelsIgnoredChannels = 300;
            public int chatterbotChannels = 300;
        }
    }
}
