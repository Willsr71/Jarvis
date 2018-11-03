package sr.will.jarvis.modules.levels.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.levels.ModuleLevels;
import sr.will.jarvis.modules.levels.XPUser;

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

        HashMap<Integer, XPUser> leaderboard = module.getLeaderboard(message.getGuild().getIdLong());

        int maxLen = 0;

        for (XPUser xpUser : leaderboard.values()) {
            Member member = Jarvis.getJda().getGuildById(xpUser.guildId).getMemberById(xpUser.userId);

            if (member == null) {
                continue;
            }

            maxLen = Math.max(member.getEffectiveName().length(), maxLen);
        }

        StringBuilder stringBuilder = new StringBuilder();
        int realpos = 0;
        for (int pos : leaderboard.keySet()) {
            XPUser xpUser = leaderboard.get(pos);
            Member member = Jarvis.getJda().getGuildById(xpUser.guildId).getMemberById(xpUser.userId);

            if (member != null) {
                realpos += 1;
                int userLevel = module.getLevelFromXp(xpUser.xp);
                long levelXp = module.getLevelXp(userLevel);
                long nextLevelXp = module.getLevelXp(userLevel + 1);
                long userLevelXp = xpUser.xp - levelXp;

                stringBuilder.append("`").append(realpos).append(getFiller(1 - (realpos + "").length())).append("` ");
                stringBuilder.append("`").append(member.getEffectiveName()).append(getFiller(maxLen - member.getEffectiveName().length())).append("`");
                stringBuilder.append(" ").append("Level ").append(userLevel);
                stringBuilder.append(" (").append(userLevelXp).append("/").append(nextLevelXp).append(" (tot ").append(xpUser.xp).append("))");
                stringBuilder.append("\n");
            }

            if ((realpos % 10 == 0 || pos == leaderboard.size()) && stringBuilder.length() != 0) {
                EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
                embed.setDescription(stringBuilder.toString());
                message.getChannel().sendMessage(embed.build()).queue();
                stringBuilder = new StringBuilder();
            }
        }
    }
}
