package sr.will.jarvis.cache;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.manager.Stats;
import sr.will.jarvis.thread.JarvisThread;

import java.util.ArrayList;

import static java.util.stream.Collectors.toList;

public class Cache {
    private static final ArrayList<CacheEntry> cacheEntries = new ArrayList<>();
    private static JarvisThread cleanupThread;

    public Cache() {

    }

    public void start() {
        cleanupThread = new JarvisThread(null, this::cleanupCache)
                .name("CacheCleanup")
                .repeat(true, Jarvis.getInstance().config.cache.cleanupInterval * 1000)
                .silent(true);
        cleanupThread.start();
    }

    public void stop() {
        if (cleanupThread != null) {
            cleanupThread.kill();
        }
    }

    public void restart() {
        stop();
        start();
    }

    private void cleanupCache() {
        synchronized (cacheEntries) {
            cacheEntries.removeIf(CacheEntry::isExpired);
        }
    }


    public static <T> ArrayList<T> getByType(Class<T> type) {
        synchronized (cacheEntries) {
            return (ArrayList<T>) cacheEntries.stream()
                    .filter(type::isInstance)
                    .map(type::cast)
                    .collect(toList());
        }
    }

    public static void addEntry(CacheEntry entry) {
        synchronized (cacheEntries) {
            // Remove duplicate entries
            int before = cacheEntries.size();
            cacheEntries.removeIf(entry1 -> (entry.matches(entry1)));
            if (cacheEntries.size() < before) {
                if (before - cacheEntries.size() == 1) {
                    Jarvis.getLogger().debug("Removed duplicate cache entry of type {}", entry.getClass().getSimpleName());
                } else {
                    Jarvis.getLogger().info("Removed multiple duplicate cache entries ({}) of type {}", before - cacheEntries.size(), entry.getClass().getSimpleName());
                }
            }

            // Stats for entry type
            if (getByType(entry.getClass()).size() == 0) {
                Stats.addGauge("cache." + entry.getClass().getSimpleName(), () -> getByType(entry.getClass()).size());
            }

            // Add entry
            cacheEntries.add(entry);
        }
    }

    public static void removeEntry(CacheEntry entry) {
        synchronized (cacheEntries) {
            cacheEntries.remove(entry);
        }
    }
}
