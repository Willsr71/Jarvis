package sr.will.jarvis.module.levels.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.levels.ModuleLevels;

import java.awt.*;

public class CommandRank extends Command {
    private ModuleLevels module;

    public CommandRank(ModuleLevels module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        User user = getMentionedUser(message, args);
        if (user == null) {
            user = message.getAuthor();
        }

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        embed.setAuthor(message.getGuild().getMember(user).getEffectiveName(), null, user.getEffectiveAvatarUrl());

        long userXp = module.getUserXp(message.getGuild().getIdLong(), user.getIdLong());
        int userLevel = module.getLevelFromXp(userXp);
        long levelXp = module.getLevelXp(userLevel);
        long nextLevelXp = module.getLevelXp(userLevel + 1);
        long userLevelXp = userXp - levelXp;
        int userRank = module.getLeaderboardPosition(message.getGuild().getIdLong(), user.getIdLong());

        embed.addField("Rank", userRank + "", true);
        embed.addField("Lvl", userLevel + "", true);
        embed.addField("Exp", userLevelXp + "/" + nextLevelXp + " (tot " + userXp + ")", true);

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
