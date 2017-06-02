package sr.will.jarvis.rest.mee6;

import java.util.ArrayList;

public class Levels {
    public ArrayList<Player> players;
    public Server server;

    public class Player {
        public String avatar;
        public int discriminator;
        public long id;
        public int lvl;
        public long lvl_xp;
        public String name;
        public long total_xp;
        public long xp;
        public int xp_percent;
    }

    public class Server {
        public String icon;
        public long id;
        public String name;
    }
}
