package sr.will.jarvis.modules.chatbot;

import net.noxal.common.cache.Cache;
import net.noxal.common.cache.CacheEntry;
import sr.will.jarvis.Jarvis;

public class CachedChatterbotChannel extends CacheEntry {
    public long channelId;
    private boolean chatterbotChannel;

    public CachedChatterbotChannel(long channelId, boolean chatterbotChannel) {
        super(Jarvis.getInstance().config.cache.timeouts.chatterbotChannels);
        this.channelId = channelId;
        this.chatterbotChannel = chatterbotChannel;
    }

    public boolean isChatterbotChannel() {
        updateLastUsed();
        return chatterbotChannel;
    }

    public static CachedChatterbotChannel getEntry(long channelId) {
        for (CachedChatterbotChannel cachedChatterbotChannel : Cache.getByType(CachedChatterbotChannel.class)) {
            if (cachedChatterbotChannel.channelId == channelId) {
                return cachedChatterbotChannel;
            }
        }

        return null;
    }

    public boolean fieldsMatch(CacheEntry entry) {
        CachedChatterbotChannel c = (CachedChatterbotChannel) entry;
        return channelId == c.channelId;
    }
}
