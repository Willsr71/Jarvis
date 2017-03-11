package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

import java.awt.*;
import java.util.HashMap;

public class CommandBanList extends Command {
    private Jarvis jarvis;

    public CommandBanList(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        HashMap<String, Long> bans = jarvis.banManager.getBans(message.getGuild().getId());

        EmbedBuilder embed = new EmbedBuilder().setTitle("Active bans", "https://jarvis.will.sr").setColor(Color.GREEN);

        if (bans.size() == 0) {
            embed.setDescription("None");
        } else {
            for (String userId : bans.keySet()) {
                Member member = message.getGuild().getMemberById(userId);
                String memberName = userId;
                if (member != null) {
                    memberName = member.getEffectiveName();
                }

                embed.addField(memberName, DateUtils.formatDateDiff(bans.get(userId)), true);
            }
        }

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
