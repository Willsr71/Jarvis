package sr.will.jarvis.config;

import java.util.ArrayList;

public class Mutes {
    public ArrayList<Mute> mutes = new ArrayList<>();

    public class Mute {
        public String userId;
        public String guildId;
    }
}
