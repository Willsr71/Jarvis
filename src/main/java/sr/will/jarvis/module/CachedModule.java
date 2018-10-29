package sr.will.jarvis.module;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.cache.Cache;
import sr.will.jarvis.cache.CacheEntry;

public class CachedModule extends CacheEntry {
    public long guildId;
    public String module;
    private boolean enabled;

    public CachedModule(long guildId, String module, boolean enabled) {
        super(Jarvis.getInstance().config.cache.moduleCacheTimeout, "cached_module_usage");
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
}
