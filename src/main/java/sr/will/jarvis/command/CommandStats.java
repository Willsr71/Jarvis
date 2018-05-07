package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.manager.Stats;

import java.awt.*;

public class CommandStats extends Command {
    private Jarvis jarvis;

    public CommandStats(Jarvis jarvis) {
        super("stats", "stats", "Displays stats about the bot instance", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        Runtime runtime = Runtime.getRuntime();
        EmbedBuilder embed = new EmbedBuilder().setTitle("Stats", null).setColor(Color.GREEN);

        embed.addField("Uptime", DateUtils.formatDateDiff(Stats.startTime), true);
        embed.addField("Ping", message.getJDA().getPing() + "ms", true);
        embed.addField("Guilds", message.getJDA().getGuilds().size() + "", true);
        embed.addField("Text channels", message.getJDA().getTextChannels().size() + "", true);
        embed.addField("Voice channels", message.getJDA().getVoiceChannels().size() + "", true);
        embed.addField("Users", message.getJDA().getUsers().size() + "", true);
        embed.addField("Events processed", Stats.eventsProcessed + "", true);
        embed.addField("Messages processed", Stats.messagesProcessed + "", true);
        embed.addField("Queries processed", Stats.queriesProcessed + "", true);
        embed.addField("Events/minute", Stats.getDataInStatsInterval("events") / 30 + "", true);
        embed.addField("Messages/minute", Stats.getDataInStatsInterval("messages") / 30 + "", true);
        embed.addField("Queries/minute", Stats.getDataInStatsInterval("queries") / 30 + "", true);
        embed.addField("Threads", Thread.activeCount() + "", true);
        embed.addField("Memory", ((runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)) + "MB / " + (runtime.maxMemory() / (1024 * 1024)) + "MB", true);
        embed.addField("", "", true);

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
