package sr.will.jarvis.modules.admin.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.admin.ModuleAdmin;

public class CommandUnban extends Command {
    private ModuleAdmin module;

    public CommandUnban(ModuleAdmin module) {
        super("unban", "unban <user mention|user id>", "Unbans the specified member", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkBotPermission(message, Permission.BAN_MEMBERS);
        checkUserPermission(message, Permission.BAN_MEMBERS);

        User user = getMentionedUser(message, args);
        if (user == null) {
            sendFailureMessage(message, "No user tagged");
            return;
        }

        if (!module.banManager.isBanned(message.getGuild().getIdLong(), user.getIdLong())) {
            sendFailureMessage(message, "User is not banned");
            return;
        }

        sendSuccessMessage(message, user.getAsMention() + " has been unbanned");
        module.banManager.unban(message.getGuild().getIdLong(), user.getIdLong());
    }
}
