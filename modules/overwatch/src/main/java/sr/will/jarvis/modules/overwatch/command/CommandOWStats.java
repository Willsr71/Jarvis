package sr.will.jarvis.modules.overwatch.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.overwatch.ModuleOverwatch;
import sr.will.jarvis.rest.owapi.UserBlob;

import java.awt.*;
import java.util.Date;

public class CommandOWStats extends Command {
    private ModuleOverwatch module;

    public CommandOWStats(ModuleOverwatch module) {
        super("owstats", "owstats [user mention|user id|battletag]", "Shows the user's level, current competitive rank, and the top three heroes for quick play and competitive by playtime", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        long startTime = new Date().getTime();

        UserBlob userBlob = module.getUserBlob(message, args);
        if (userBlob == null) {
            return;
        }

        UserBlob.Region.Stats.Mode.OverallStats overallStats = userBlob.getRegion().stats.quickplay.overall_stats;
        String userUrl = "https://playoverwatch.com/en-us/career/pc/us/" + userBlob.battletag;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor(userBlob.battletag, userUrl, module.getTierImage(overallStats.tier))
                .addField("Level", ((overallStats.prestige * 100) + overallStats.level) + "", true)
                .addField("SR", overallStats.comprank + "", true)
                .addField("Top heroes (QP)", module.getTopHeroesAsString(userBlob.getRegion().heroes.playtime.quickplay, 3), true)
                .addField("Top heroes (Comp)", (userBlob.getRegion().heroes.playtime.competitive == null) ? "N/A" : module.getTopHeroesAsString(userBlob.getRegion().heroes.playtime.competitive, 3), true)
                .setThumbnail(overallStats.avatar)
                .setFooter("Returned in " + (new Date().getTime() - startTime) + "ms", null);
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
