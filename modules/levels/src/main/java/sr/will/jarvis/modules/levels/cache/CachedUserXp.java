package sr.will.jarvis.modules.levels.cache;

import net.noxal.common.cache.Cache;
import net.noxal.common.cache.CacheEntry;
import sr.will.jarvis.Jarvis;

public class CachedUserXp extends CacheEntry {
    public long guildId;
    public long userId;
    private long xp;

    public CachedUserXp(long guildId, long userId, long xp) {
        super(Jarvis.getInstance().config.cache.timeouts.userXp);
        this.guildId = guildId;
        this.userId = userId;
        this.xp = xp;
    }

    public long getXp() {
        updateLastUsed();
        return xp;
    }

    public void addXp(long toAdd) {
        this.xp = xp + toAdd;
    }

    public static CachedUserXp getEntry(long guildId, long userId) {
        for (CachedUserXp cachedUserXp : Cache.getByType(CachedUserXp.class)) {
            if (cachedUserXp.guildId == guildId && cachedUserXp.userId == userId) {
                return cachedUserXp;
            }
        }

        return null;
    }

    public boolean fieldsMatch(CacheEntry entry) {
        CachedUserXp c = (CachedUserXp) entry;
        return guildId == c.guildId && userId == c.userId;
    }
}
