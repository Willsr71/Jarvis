package sr.will.jarvis.modules.verify;

public class GuildVerificationData {
    public long channelId;
    public long roleId;

    public GuildVerificationData(long channelId, long roleId) {
        this.channelId = channelId;
        this.roleId = roleId;
    }
}
