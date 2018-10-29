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
        embed.addField("Version", Jarvis.VERSION, true);

        embed.setDescription("Stats: https://enterprise.will.sr/grafana");

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
