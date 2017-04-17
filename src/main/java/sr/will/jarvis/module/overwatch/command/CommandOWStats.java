package sr.will.jarvis.module.overwatch.command;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.overwatch.ModuleOverwatch;
import sr.will.jarvis.rest.owapi.UserBlob;

import java.awt.*;
import java.util.HashMap;

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
            if (module.isValidBattleTag(args[0])) {
                battletag = args[0].replace("#", "-");
            } else {
                battletag = module.getBattletag(getMentionedUser(message, args).getId());
            }
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

        UserBlob userBlob;
        try {
            userBlob = module.getUserBlob(battletag);
        } catch (UnirestException e) {
            e.printStackTrace();
            sendFailureMessage(message, "An error occurred");
            return;
        }

        if (userBlob.error != null) {
            sendFailureMessage(message, capitalizeProperly(userBlob.msg));
            return;
        }

        UserBlob.Region.Stats.Mode.OverallStats overallStats = userBlob.getRegion().stats.quickplay.overall_stats;
        String userUrl = "https://playoverwatch.com/en-us/career/pc/us/" + battletag;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor(battletag, userUrl, overallStats.avatar)
                .addField("Level", ((overallStats.prestige * 100) + overallStats.level) + "", true)
                .addField("SR", overallStats.comprank + "", true)
                .addField("Top heroes (Comp)", getTopHeroes(userBlob.getRegion().heroes.playtime.competitive), true)
                .addField("Top heroes (QP)", getTopHeroes(userBlob.getRegion().heroes.playtime.quickplay), true)
                .setThumbnail(module.getTierImage(overallStats.tier));
        message.getChannel().sendMessage(embed.build()).queue();
    }

    public String getTopHeroes(HashMap<String, Double> heroes) {
        HashMap<String, Double> topHeroes = module.sortHeroesByTime(heroes);
        String topHeroesString = "";
        int x = 0;
        for (String hero : topHeroes.keySet()) {
            topHeroesString += capitalizeProperly(hero) + " (" + topHeroes.get(hero).intValue() + " hours)\n";

            x += 1;
            if (x >= 3) {
                break;
            }
        }

        return topHeroesString;
    }
}
