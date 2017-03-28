package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandLeaderboard extends Command {
    private Jarvis jarvis;

    public CommandLeaderboard(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        HashMap<Long, ArrayList<String>> leaderboard = jarvis.levelManager.getLeaderboard(message.getGuild().getId());

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);

        for (long xp : leaderboard.keySet()) {
            System.out.println(xp);

            for (String user : leaderboard.get(xp)) {
                embed.addField(jarvis.getJda().getUserById(user).getName(), "Level " + jarvis.levelManager.getLevelFromXp(xp) + " (" + xp + "xp)", false);
            }
        }

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
