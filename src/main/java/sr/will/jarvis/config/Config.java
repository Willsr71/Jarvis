package sr.will.jarvis.config;

import java.util.ArrayList;

public class Config {
    public Discord discord = new Discord();
    public Sql sql = new Sql();
    public boolean debug;

    public class Discord {
        public String token;
        public ArrayList<String> owners;
        public long statusMessageInterval;
        public ArrayList<String> statusMessages;
        public ArrayList<String> pinEmotes;
    }

    public class Sql {
        public String host;
        public String database;
        public String user;
        public String password;
    }
}
