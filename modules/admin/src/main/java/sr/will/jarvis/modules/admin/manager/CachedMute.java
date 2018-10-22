package sr.will.jarvis.modules.admin.manager;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.manager.Stats;

class CachedMute {
    long guildId;
    long userId;
    long duration;
    private long lastUsed;

    CachedMute(long guildId, long userId, long duration) {
        this.guildId = guildId;
        this.userId = userId;
        this.duration = duration;
        updateLastUsed();
    }

    boolean isExpired() {
        // If it has been longer than the cacheTimeout, remove from cache list
        if ((System.currentTimeMillis() - lastUsed) / 1000 >= Jarvis.getInstance().config.cache.muteCacheTimeout) {
            return true;
        }

        return false;
    }

    public void updateLastUsed() {
        lastUsed = System.currentTimeMillis();
        Stats.incrementCounter("admin.cached_mutes_usage");
    }
}
