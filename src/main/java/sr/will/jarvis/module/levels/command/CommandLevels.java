package sr.will.jarvis.module.levels.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.levels.ModuleLevels;

import java.awt.*;
import java.util.HashMap;

public class CommandLevels extends Command {
    private ModuleLevels module;

    public CommandLevels(ModuleLevels module) {
        super("leaderboard", "leaderboard", "Displays the experience leaderboard of the members of the guild", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        HashMap<Integer, ModuleLevels.XPUser> leaderboard = module.getLeaderboard(message.getGuild().getIdLong());

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        embed.setDescription(getLeaderboardString(leaderboard));

        message.getChannel().sendMessage(embed.build()).queue();
    }

    private String getLeaderboardString(HashMap<Integer, ModuleLevels.XPUser> leaderboard) {
        int maxLen = 0;

        for (ModuleLevels.XPUser xpUser : leaderboard.values()) {
            Member member = Jarvis.getJda().getGuildById(xpUser.guildId).getMemberById(xpUser.userId);
            maxLen = Math.max(member.getEffectiveName().length(), maxLen);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int pos : leaderboard.keySet()) {
            ModuleLevels.XPUser xpUser = leaderboard.get(pos);
            Member member = Jarvis.getJda().getGuildById(xpUser.guildId).getMemberById(xpUser.userId);

            long userXp = xpUser.xp;
            int userLevel = module.getLevelFromXp(userXp);
            long levelXp = module.getLevelXp(userLevel);
            long nextLevelXp = module.getLevelXp(userLevel + 1);
            long userLevelXp = userXp - levelXp;

            stringBuilder.append("`").append(pos).append(getFiller(1 - (pos + "").length())).append("` ");
            stringBuilder.append("`").append(member.getEffectiveName()).append(getFiller(maxLen - member.getEffectiveName().length())).append("`");
            stringBuilder.append(" ").append("Level ").append(userLevel);
            stringBuilder.append(" (").append(userLevelXp).append("/").append(nextLevelXp).append(" (tot ").append(userXp).append("))");
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
