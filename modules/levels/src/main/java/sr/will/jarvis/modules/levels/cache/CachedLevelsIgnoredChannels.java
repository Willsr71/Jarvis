package sr.will.jarvis.modules.levels.cache;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.cache.Cache;
import sr.will.jarvis.cache.CacheEntry;

public class CachedLevelsIgnoredChannels extends CacheEntry {
    public long channelId;
    private boolean ignored;

    public CachedLevelsIgnoredChannels(long channelId, boolean ignored) {
        this.channelId = channelId;
        this.ignored = ignored;
        initialize(Jarvis.getInstance().config.cache.timeouts.levelsIgnoredChannels);
    }

    public boolean isIgnored() {
        updateLastUsed();
        return ignored;
    }

    public static CachedLevelsIgnoredChannels getEntry(long channelId) {
        for (CachedLevelsIgnoredChannels cachedLevelsIgnoredChannels : Cache.getByType(CachedLevelsIgnoredChannels.class)) {
            if (cachedLevelsIgnoredChannels.channelId == channelId) {
                return cachedLevelsIgnoredChannels;
            }
        }

        return null;
    }

    public boolean fieldsMatch(CacheEntry entry) {
        CachedLevelsIgnoredChannels c = (CachedLevelsIgnoredChannels) entry;
        return channelId == c.channelId;
    }
}
