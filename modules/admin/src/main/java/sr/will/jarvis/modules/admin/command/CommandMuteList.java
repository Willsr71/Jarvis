package sr.will.jarvis.modules.admin.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.admin.ModuleAdmin;

import java.awt.*;
import java.util.HashMap;

public class CommandMuteList extends Command {
    private ModuleAdmin module;

    public CommandMuteList(ModuleAdmin module) {
        super("mutelist", "mutelist", "Displays the currently mutes users and remaining durations", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        HashMap<Long, Long> mutes = module.muteManager.getMutes(message.getGuild().getIdLong());

        EmbedBuilder embed = new EmbedBuilder().setTitle("Active mutes", null).setColor(Color.GREEN);

        if (mutes.size() == 0) {
            embed.setDescription("None");
        } else {
            for (long userId : mutes.keySet()) {
                String userName = String.valueOf(userId);

                User user = message.getJDA().getUserById(userId);
                if (user != null) {
                    userName = user.getName();

                    Member member = message.getGuild().getMember(user);
                    if (member != null) {
                        userName = member.getEffectiveName();
                    }
                }

                embed.addField(userName, DateUtils.formatDateDiff(mutes.get(userId)), false);
            }
        }

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
