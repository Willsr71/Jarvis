package sr.will.jarvis.module.admin.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.admin.ModuleAdmin;
import sr.will.jarvis.util.CommandUtils;

import java.awt.*;

public class CommandBanTime extends Command {
    private ModuleAdmin module;

    public CommandBanTime(ModuleAdmin module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        User user = CommandUtils.getMentionedUser(message, args);
        if (user == null) {
            CommandUtils.sendFailureMessage(message, "No user tagged");
            return;
        }

        long duration = module.banManager.getBanDuration(message.getGuild().getId(), user.getId());

        if (!DateUtils.timestampApplies(duration)) {
            CommandUtils.sendSuccessMessage(message, "User not banned", false);
            return;
        }

        message.getChannel().sendMessage(new EmbedBuilder().setTitle("Success", "https://jarvis.will.sr").setColor(Color.GREEN).addField(user.getName(), DateUtils.formatDateDiff(duration), true).build()).queue();
    }
}
