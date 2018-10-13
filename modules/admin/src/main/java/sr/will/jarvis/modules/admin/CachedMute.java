package sr.will.jarvis.modules.admin;

public class CachedMute {
    public long guildId;
    public long userId;
    public long duration;
    private long lastUsed;

    public CachedMute(long guildId, long userId, long duration) {
        this.guildId = guildId;
        this.userId = userId;
        this.duration = duration;
    }

    /*
    public boolean cleanup() {
        // If it has been longer than the cacheTimeout, remove from cache list
        if ((new Date().getTime() - lastUsed) / 1000 >= plugin.config.getLong("offlinePlayerCacheTimeout")) {
            System.out.println("Removed mute from cache");
            plugin.cachedOfflinePlayers.remove(getUUID());
            return true;
        }

        return false;
    }
    */
}
