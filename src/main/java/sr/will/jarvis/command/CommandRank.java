package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

import java.awt.*;

public class CommandRank extends Command {
    private Jarvis jarvis;

    public CommandRank(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        embed.setAuthor(message.getGuild().getMember(message.getAuthor()).getEffectiveName(), null, message.getAuthor().getEffectiveAvatarUrl());

        long userXp = jarvis.levelManager.getUserXp(message.getGuild().getId(), message.getAuthor().getId());
        int userLevel = jarvis.levelManager.getLevelFromXp(userXp);
        long levelXp = jarvis.levelManager.getLevelXp(userLevel);
        long nextLevelXp = jarvis.levelManager.getLevelXp(userLevel + 1);
        long userLevelXp = userXp - levelXp;

        embed.addField("Rank", "N/A", true);
        embed.addField("Lvl", userLevel + "", true);
        embed.addField("Exp", userLevelXp + "/" + nextLevelXp + " (tot " + userXp + ")", true);

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
