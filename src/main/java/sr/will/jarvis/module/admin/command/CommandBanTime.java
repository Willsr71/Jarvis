package sr.will.jarvis.module.admin.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.admin.ModuleAdmin;

import java.awt.*;

public class CommandBanTime extends Command {
    private ModuleAdmin module;

    public CommandBanTime(ModuleAdmin module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        User user = getMentionedUser(message, args);
        if (user == null) {
            sendFailureMessage(message, "No user tagged");
            return;
        }

        long duration = module.banManager.getBanDuration(message.getGuild().getIdLong(), user.getIdLong());

        if (!DateUtils.timestampApplies(duration)) {
            sendSuccessMessage(message, "User not banned", false);
            return;
        }

        message.getChannel().sendMessage(new EmbedBuilder().setTitle("Success", "https://jarvis.will.sr").setColor(Color.GREEN).addField(user.getName(), DateUtils.formatDateDiff(duration), true).build()).queue();
    }

    @Override
    public String getUsage() {
        return "bantime <user mention|user id>";
    }

    @Override
    public String getDescription() {
        return "Displays the remaining duration of the mentioned user's ban";
    }

    @Override
    public boolean getModuleEnabled(long guildId) {
        return module.isEnabled(guildId);
    }
}
