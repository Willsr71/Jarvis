package sr.will.jarvis.module.admin.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.admin.ModuleAdmin;
import sr.will.jarvis.util.CommandUtils;

public class CommandUnban extends Command {
    private ModuleAdmin module;

    public CommandUnban(ModuleAdmin module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.BAN_MEMBERS)) {
            CommandUtils.sendFailureMessage(message, "You don't have permission for that");
            return;
        }

        User user = CommandUtils.getMentionedUser(message, args);
        if (user == null) {
            CommandUtils.sendFailureMessage(message, "No user tagged");
            return;
        }

        if (!module.banManager.isBanned(message.getGuild().getId(), user.getId())) {
            CommandUtils.sendFailureMessage(message, "User is not banned");
            return;
        }

        CommandUtils.sendSuccessMessage(message, user.getAsMention() + " has been unbanned");
        module.banManager.unban(message.getGuild().getId(), user.getId());
    }
}
