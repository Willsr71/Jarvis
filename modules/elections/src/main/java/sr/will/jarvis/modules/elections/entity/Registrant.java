package sr.will.jarvis.modules.elections.entity;

import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;

public class Registrant {
    public long userId;
    public int votes = 0;

    public Registrant(long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return Jarvis.getJda().getUserById(userId);
    }

    public void addVote() {
        votes += 1;
    }
}
