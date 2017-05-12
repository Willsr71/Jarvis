package sr.will.jarvis.module.overwatch.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.overwatch.ModuleOverwatch;
import sr.will.jarvis.rest.owapi.UserBlob;

import java.awt.*;

public class CommandOWHeroes extends Command {
    private ModuleOverwatch module;

    public CommandOWHeroes(ModuleOverwatch module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        UserBlob userBlob = module.getUserBlob(message, args);
        if (userBlob == null) {
            return;
        }

        UserBlob.Region.Stats.Mode.OverallStats overallStats = userBlob.getRegion().stats.quickplay.overall_stats;
        String userUrl = "https://playoverwatch.com/en-us/career/pc/us/" + userBlob.battletag;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor(userBlob.battletag, userUrl, module.getTierImage(overallStats.tier))
                .addField("Top heroes (QP)", module.getTopHeroesAsString(userBlob.getRegion().heroes.playtime.quickplay), true)
                .addField("Top heroes (Comp)", (userBlob.getRegion().heroes.playtime.competitive == null) ? "N/A" : module.getTopHeroesAsString(userBlob.getRegion().heroes.playtime.competitive), true)
                .setThumbnail(overallStats.avatar);
        message.getChannel().sendMessage(embed.build()).queue();
    }

    @Override
    public String getUsage() {
        return "owheroes [user mention|user id|battletag]";
    }

    @Override
    public String getDescription() {
        return "Shows the user's quickplay and current competitive heroes in order of playtime";
    }

    @Override
    public boolean getModuleEnabled(long guildId) {
        return module.isEnabled(guildId);
    }
}
