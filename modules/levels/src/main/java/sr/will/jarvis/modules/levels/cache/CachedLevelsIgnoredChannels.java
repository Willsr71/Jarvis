package sr.will.jarvis.modules.levels.cache;

import net.noxal.common.cache.Cache;
import net.noxal.common.cache.CacheEntry;
import sr.will.jarvis.Jarvis;

public class CachedLevelsIgnoredChannels extends CacheEntry {
    public long channelId;
    private boolean ignored;

    public CachedLevelsIgnoredChannels(long channelId, boolean ignored) {
        super(Jarvis.getInstance().config.cache.timeouts.levelsIgnoredChannels);
        this.channelId = channelId;
        this.ignored = ignored;
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
