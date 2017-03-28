package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.util.CommandUtils;

import java.awt.*;

public class CommandRank extends Command {
    private Jarvis jarvis;

    public CommandRank(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        User user = CommandUtils.getMentionedUser(message, args);
        if (user == null) {
            user = message.getAuthor();
        }

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        embed.setAuthor(message.getGuild().getMember(user).getEffectiveName(), null, user.getEffectiveAvatarUrl());

        long userXp = jarvis.levelManager.getUserXp(message.getGuild().getId(), user.getId());
        int userLevel = jarvis.levelManager.getLevelFromXp(userXp);
        long levelXp = jarvis.levelManager.getLevelXp(userLevel);
        long nextLevelXp = jarvis.levelManager.getLevelXp(userLevel + 1);
        long userLevelXp = userXp - levelXp;
        int userRank = jarvis.levelManager.getLeaderboardPosition(message.getGuild().getId(), user.getId());

        embed.addField("Rank", userRank + "", true);
        embed.addField("Lvl", userLevel + "", true);
        embed.addField("Exp", userLevelXp + "/" + nextLevelXp + " (tot " + userXp + ")", true);

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
