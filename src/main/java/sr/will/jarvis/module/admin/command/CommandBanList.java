package sr.will.jarvis.module.admin.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.admin.ModuleAdmin;

import java.awt.*;
import java.util.HashMap;

public class CommandBanList extends Command {
    private ModuleAdmin module;

    public CommandBanList(ModuleAdmin module) {
        super("banlist", "banlist", "Displays the currently banned users and remaining durations", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        HashMap<Long, Long> bans = module.banManager.getBans(message.getGuild().getIdLong());

        EmbedBuilder embed = new EmbedBuilder().setTitle("Active bans", "https://jarvis.will.sr").setColor(Color.GREEN);

        if (bans.size() == 0) {
            embed.setDescription("None");
        } else {
            for (long userId : bans.keySet()) {
                String userName = String.valueOf(userId);

                User user = message.getJDA().getUserById(userId);
                if (user != null) {
                    userName = user.getName();
                }

                embed.addField(userName, DateUtils.formatDateDiff(bans.get(userId)), true);
            }
        }

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
