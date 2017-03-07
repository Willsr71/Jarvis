package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

import java.awt.*;

public class CommandStats extends Command {
    private Jarvis jarvis;

    public CommandStats(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("Stats").setColor(Color.GREEN);

        embed.addField("Uptime", DateUtils.formatDateDiff(jarvis.startTime), true);
        embed.addField("Guilds", message.getJDA().getGuilds().size() + "", true);
        embed.addField("Text channels", message.getJDA().getTextChannels().size() + "", true);
        embed.addField("Voice channels", message.getJDA().getVoiceChannels().size() + "", true);
        embed.addField("Users", message.getJDA().getUsers().size() + "", true);
        embed.addField("Messages sent", jarvis.messagesReceived + "", true);

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
