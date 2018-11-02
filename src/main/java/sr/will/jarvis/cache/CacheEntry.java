package sr.will.jarvis.cache;

import sr.will.jarvis.manager.Stats;

public abstract class CacheEntry {
    private long cacheTimeout;

    private long lastUsed;

    public void initialize(long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
        updateLastUsed();
        Cache.addEntry(this);
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() - lastUsed) / 1000 >= cacheTimeout;
    }

    protected void updateLastUsed() {
        lastUsed = System.currentTimeMillis();
        Stats.incrementCounter("cache.usage." + this.getClass().getSimpleName());
    }

    public boolean matches(CacheEntry entry) {
        if (!this.getClass().isInstance(entry)) return false;
        return fieldsMatch(entry);
    }

    public abstract boolean fieldsMatch(CacheEntry entry);
}
