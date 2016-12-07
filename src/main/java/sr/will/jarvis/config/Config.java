package sr.will.jarvis.config;

public class Config {
    public Discord discord = new Discord();
    public Sql sql = new Sql();

    public class Discord {
        public String token;
        public String game;
    }

    public class Sql {
        public String host;
        public String database;
        public String user;
        public String password;
    }
}
