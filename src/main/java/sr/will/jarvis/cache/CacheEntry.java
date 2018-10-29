package sr.will.jarvis.cache;

import sr.will.jarvis.manager.Stats;

public abstract class CacheEntry {
    private long cacheTimeout;
    private String statsCounter;

    private long lastUsed;

    public CacheEntry(long cacheTimeout, String statsCounter) {
        this.cacheTimeout = cacheTimeout;
        this.statsCounter = statsCounter;
        updateLastUsed();
        Cache.addEntry(this);
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() - lastUsed) / 1000 >= cacheTimeout;
    }

    protected void updateLastUsed() {
        lastUsed = System.currentTimeMillis();
        Stats.incrementCounter(statsCounter);
    }
}
