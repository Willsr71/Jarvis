package sr.will.jarvis.module.levels.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.levels.ModuleLevels;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandLeaderboard extends Command {
    private ModuleLevels module;

    public CommandLeaderboard(ModuleLevels module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        HashMap<Long, ArrayList<String>> leaderboard = module.getLeaderboard(message.getGuild().getId());

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);

        for (long xp : leaderboard.keySet()) {
            System.out.println(xp);

            for (String user : leaderboard.get(xp)) {
                embed.addField(Jarvis.getJda().getUserById(user).getName(), "Level " + module.getLevelFromXp(xp) + " (" + xp + "xp)", false);
            }
        }

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
