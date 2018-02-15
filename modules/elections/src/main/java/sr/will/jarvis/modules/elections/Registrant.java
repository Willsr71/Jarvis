package sr.will.jarvis.modules.elections;

import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;

public class Registrant {
    public long userId;
    public int votes;
    public int position;

    public Registrant(long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return Jarvis.getJda().getUserById(userId);
    }

    public void addVote() {
        votes += 1;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
