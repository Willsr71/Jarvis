package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

public class CommandStats extends Command {
    private Jarvis jarvis;

    public CommandStats(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        String stats = "```yaml";
        stats += "\nUptime: " + DateUtils.formatDateDiff(jarvis.startTime);
        stats += "\nGuilds: " + message.getJDA().getGuilds().size();
        stats += "\nText_channels: " + message.getJDA().getTextChannels().size();
        stats += "\nVoice_channels: " + message.getJDA().getVoiceChannels().size();
        stats += "\nUsers: " + message.getJDA().getUsers().size();
        stats += "\nMessages_sent: " + jarvis.messagesReceived;
        stats += "```";

        message.getChannel().sendMessage(stats).queue();
    }
}
