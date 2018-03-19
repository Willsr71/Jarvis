package sr.will.jarvis.modules.overwatch.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.overwatch.ModuleOverwatch;
import sr.will.jarvis.modules.overwatch.rest.ovrstat.UserInfo;

import java.awt.*;

public class CommandOWStats extends Command {
    private ModuleOverwatch module;

    public CommandOWStats(ModuleOverwatch module) {
        super("owstats", "owstats [user mention|user id|battletag]", "Shows the user's level, current competitive rank, and the top three heroes for quick play and competitive by playtime", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        long startTime = System.currentTimeMillis();

        UserInfo userInfo = module.getUserInfo(message, args);
        if (userInfo == null) {
            return;
        }

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        if (userInfo.rating != 0) {
            embed
                    .setAuthor(userInfo.battletag, userInfo.playOverwatchUrl, userInfo.ratingIcon)
                    .addField("Level", ((userInfo.prestige * 100) + userInfo.level) + "", true)
                    .addField("SR", userInfo.rating + "", true)
                    .addField("Games (QP)", userInfo.quickPlayStats.careerStats.get("allHeroes").game.gamesWon + " (" + userInfo.quickPlayStats.careerStats.get("allHeroes").game.timePlayed + ")", true)
                    .addField("W/D/L (Comp)", userInfo.competitiveStats.careerStats.get("allHeroes").game.gamesWon + "/" + userInfo.competitiveStats.careerStats.get("allHeroes").game.gamesTied + "/" + userInfo.competitiveStats.careerStats.get("allHeroes").game.gamesLost + " (" + userInfo.competitiveStats.careerStats.get("allHeroes").game.timePlayed + ")", true)
                    .addField("Top heroes (QP)", module.getTopHeroesAsString(userInfo.quickPlayStats.topHeroes, 3), true)
                    .addField("Top heroes (Comp)", module.getTopHeroesAsString(userInfo.competitiveStats.topHeroes, 3), true)
                    .setThumbnail(userInfo.icon)
                    .setFooter("Returned in " + (System.currentTimeMillis() - startTime) + "ms", null);
        } else {
            embed
                    .setAuthor(userInfo.battletag, userInfo.playOverwatchUrl)
                    .addField("Level", ((userInfo.prestige * 100) + userInfo.level) + "", true)
                    .addField("SR", "Not Placed", true)
                    .addField("Games (QP)", userInfo.quickPlayStats.careerStats.get("allHeroes").game.gamesWon + " (" + userInfo.quickPlayStats.careerStats.get("allHeroes").game.timePlayed + ")", true)
                    .addField("Top heroes (QP)", module.getTopHeroesAsString(userInfo.quickPlayStats.topHeroes, 3), true)
                    .setThumbnail(userInfo.icon)
                    .setFooter("Returned in " + (System.currentTimeMillis() - startTime) + "ms", null);
        }
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
