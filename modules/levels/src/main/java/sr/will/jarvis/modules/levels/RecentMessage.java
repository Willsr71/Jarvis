package sr.will.jarvis.modules.levels;

public class RecentMessage {
    public long guildId;
    public long channelid;
    public long userId;
    public long timestamp;
    public int messageLength;

    public RecentMessage(long guildId, long channelId, long userId, long timestamp, int messageLength) {
        this.guildId = guildId;
        this.channelid = channelId;
        this.userId = userId;
        this.timestamp = timestamp;
        this.messageLength = messageLength;
    }
}
