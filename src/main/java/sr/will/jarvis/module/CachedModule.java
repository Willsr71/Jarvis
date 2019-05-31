package sr.will.jarvis.module;

import net.noxal.common.cache.Cache;
import net.noxal.common.cache.CacheEntry;
import sr.will.jarvis.Jarvis;

public class CachedModule extends CacheEntry {
    public long guildId;
    public String module;
    private boolean enabled;

    public CachedModule(long guildId, String module, boolean enabled) {
        super(Jarvis.getInstance().config.cache.timeouts.module);
        this.guildId = guildId;
        this.module = module;
        this.enabled = enabled;
    }

    public boolean moduleEnabled() {
        updateLastUsed();
        return enabled;
    }

    public static CachedModule getEntry(long guildId, String module) {
        for (CachedModule cachedModule : Cache.getByType(CachedModule.class)) {
            if (cachedModule.guildId == guildId && cachedModule.module.equals(module)) {
                return cachedModule;
            }
        }

        return null;
    }

    public boolean fieldsMatch(CacheEntry entry) {
        CachedModule c = (CachedModule) entry;
        return guildId == c.guildId && module.equals(c.module);
    }
}