package sr.will.jarvis.module.overwatch.command;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.overwatch.ModuleOverwatch;
import sr.will.jarvis.module.overwatch.Tier;
import sr.will.jarvis.rest.owapi.UserStats;

import java.awt.*;

public class CommandOWStats extends Command {
    private ModuleOverwatch module;

    public CommandOWStats(ModuleOverwatch module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        String battletag;

        if (args.length != 0) {
            battletag = args[0].replace("#", "-");
        } else {
            battletag = module.getBattletag(message.getAuthor().getId());
        }

        if (battletag == null) {
            sendFailureMessage(message, "No battletag specified or linked");
            return;
        }

        if (!module.isValidBattleTag(battletag)) {
            sendFailureMessage(message, "Invalid battletag");
            return;
        }

        UserStats userStats;
        try {
            userStats = module.getUserStats(battletag);
        } catch (UnirestException e) {
            e.printStackTrace();
            sendFailureMessage(message, "An error occurred");
            return;
        }

        if (userStats.error != null) {
            sendFailureMessage(message, userStats.msg);
            return;
        }

        UserStats.Region.Stats.Mode.OverallStats overallStats = userStats.getRegion().stats.quickplay.overall_stats;
        String userUrl = "https://playoverwatch.com/en-" + userStats.getRegion().toString() + "/career/pc/us/" + battletag;
        Tier tier = Tier.fromSR(overallStats.comprank);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor(battletag, userUrl, overallStats.avatar)
                .addField("Level", ((overallStats.prestige * 100) + overallStats.level) + "", true)
                .addField("SR", overallStats.comprank + "", true)
                .setThumbnail(tier.getImageURL());
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
