package sr.will.jarvis.modules.admin.manager;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.cache.CacheEntry;

class CachedMute extends CacheEntry {
    long guildId;
    long userId;
    private long duration;

    CachedMute(long guildId, long userId, long duration) {
        super(Jarvis.getInstance().config.cache.muteCacheTimeout, "admin.cached_mutes_usage");
        this.guildId = guildId;
        this.userId = userId;
        this.duration = duration;
    }

    long getDuration() {
        updateLastUsed();
        return duration;
    }
}
