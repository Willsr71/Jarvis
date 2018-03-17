package sr.will.jarvis.modules.overwatch.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.overwatch.ModuleOverwatch;
import sr.will.jarvis.modules.overwatch.rest.ovrstat.UserInfo;

import java.awt.*;
import java.util.Date;

public class CommandOWHeroes extends Command {
    private ModuleOverwatch module;

    public CommandOWHeroes(ModuleOverwatch module) {
        super("owheroes", "owheroes [user mention|user id|battletag]", "Shows the user's quickplay and current competitive heroes in order of playtime", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        long startTime = new Date().getTime();

        UserInfo userInfo = module.getUserInfo(message, args);
        if (userInfo == null) {
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor(userInfo.battletag, userInfo.playOverwatchUrl, userInfo.ratingIcon)
                .addField("Top heroes (QP)", module.getTopHeroesAsString(userInfo.quickPlayStats.topHeroes), true)
                .addField("Top heroes (Comp)", module.getTopHeroesAsString(userInfo.competitiveStats.topHeroes), true)
                .setThumbnail(userInfo.icon)
                .setFooter("Returned in " + (new Date().getTime() - startTime) + "ms", null);
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
