package sr.will.jarvis.modules.levels;

import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;

public class XPUser {
    private ModuleLevels module;
    public long guildId;
    public long userId;
    public long xp;
    public int pos;

    public XPUser(ModuleLevels module, long guildId, long userId, long xp, int pos) {
        this.module = module;
        this.guildId = guildId;
        this.userId = userId;
        this.xp = xp;
        this.pos = pos;
    }

    public User getUser() {
        return Jarvis.getJda().getUserById(userId);
    }

    public int getLevel() {
        return module.getLevelFromXp(xp);
    }

    public long getLevelXp() {
        return module.getLevelXp(getLevel());
    }

    public long getNextLevelXp() {
        return module.getLevelXp(getLevel() + 1);
    }

    public long getUserLevelXp() {
        return xp - getLevelXp();
    }

    public long getNeededXp() {
        return getNextLevelXp() - getLevelXp();
    }
}
