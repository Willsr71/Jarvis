package sr.will.jarvis.cache;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.manager.Stats;
import sr.will.jarvis.thread.JarvisThread;

import java.util.ArrayList;

import static java.util.stream.Collectors.toList;

public class Cache {
    private static ArrayList<CacheEntry> cacheEntries = new ArrayList<>();
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
        int before = cacheEntries.size();

        cacheEntries.removeIf(CacheEntry::isExpired);

        if (cacheEntries.size() < before) {
            Jarvis.debug("Removed " + (before - cacheEntries.size()) + " cached items");
        }
    }


    public static <T> ArrayList<T> getByType(Class<T> type) {
        return (ArrayList<T>) cacheEntries.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(toList());
    }

    public static void addEntry(CacheEntry entry) {
        // Remove duplicate entries
        int before = cacheEntries.size();
        cacheEntries.removeIf(entry1 -> (entry.matches(entry1)));
        if (cacheEntries.size() < before) {
            if (before - cacheEntries.size() == 1) {
                Jarvis.debug("Removed duplicate cache entry of type " + entry.getClass().getSimpleName());
            } else {
                System.out.println("Removed multiple duplicate cache entries (" + (before - cacheEntries.size()) + ") of type " + entry.getClass().getSimpleName());
            }
        }

        // Stats for entry type
        if (getByType(entry.getClass()).size() == 0) {
            Stats.addGauge("cache." + entry.getClass().getSimpleName(), () -> getByType(entry.getClass()).size());
        }

        // Add entry
        cacheEntries.add(entry);
    }

    public static void removeEntry(CacheEntry entry) {
        cacheEntries.remove(entry);
    }
}
