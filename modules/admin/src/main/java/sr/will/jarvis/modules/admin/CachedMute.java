package sr.will.jarvis.modules.admin;

import net.noxal.common.cache.Cache;
import net.noxal.common.cache.CacheEntry;
import sr.will.jarvis.Jarvis;

public class CachedMute extends CacheEntry {
    public long guildId;
    public long userId;
    private long duration;

    public CachedMute(long guildId, long userId, long duration) {
        super(Jarvis.getInstance().config.cache.timeouts.mute);
        this.guildId = guildId;
        this.userId = userId;
        this.duration = duration;
    }

    public long getDuration() {
        updateLastUsed();
        return duration;
    }

    public static CachedMute getEntry(long guildId, long userId) {
        for (CachedMute cachedMute : Cache.getByType(CachedMute.class)) {
            if (cachedMute.guildId == guildId && cachedMute.userId == userId) {
                return cachedMute;
            }
        }

        return null;
    }

    public boolean fieldsMatch(CacheEntry entry) {
        CachedMute c = (CachedMute) entry;
        return guildId == c.guildId && userId == c.userId;
    }
}
