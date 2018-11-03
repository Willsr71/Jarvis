package sr.will.jarvis.modules.levels;

public class XPUser {
    private ModuleLevels module;
    public long guildId;
    public long userId;
    public long xp;
    public int pos;
    public int pos_total;

    public XPUser(ModuleLevels module, long guildId, long userId, long xp, int pos, int pos_total) {
        this.module = module;
        this.guildId = guildId;
        this.userId = userId;
        this.xp = xp;
        this.pos = pos;
        this.pos_total = pos_total;
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
}
