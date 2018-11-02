package sr.will.jarvis.modules.levels.cache;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.cache.Cache;
import sr.will.jarvis.cache.CacheEntry;

public class CachedUserXp extends CacheEntry {
    public long guildId;
    public long userId;
    private long xp;

    public CachedUserXp(long guildId, long userId, long xp) {
        this.guildId = guildId;
        this.userId = userId;
        this.xp = xp;
        initialize(Jarvis.getInstance().config.cache.timeouts.userXp);
    }

    public long getXp() {
        updateLastUsed();
        return xp;
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
