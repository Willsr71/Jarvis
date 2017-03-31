package sr.will.jarvis.module.admin.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.admin.ModuleAdmin;

public class CommandBan extends Command {
    private ModuleAdmin module;

    public CommandBan(ModuleAdmin module) {
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

        if (user.getId().equals(message.getJDA().getSelfUser().getId())) {
            sendFailureMessage(message, "You cannot ban the all powerful " + message.getGuild().getMember(message.getJDA().getSelfUser()).getEffectiveName());
            return;
        }

        if (module.banManager.isBanned(message.getGuild().getId(), user.getId())) {
            sendFailureMessage(message, "User already banned");
            return;
        }

        if (args.length == 1) {
            sendSuccessEmote(message);
            module.banManager.ban(message.getGuild().getId(), user.getId(), message.getAuthor().getId());
            return;
        }

        long duration = 0;

        try {
            duration = DateUtils.parseDateDiff(args[1], true);
        } catch (Exception e) {
            sendFailureMessage(message, "Invalid time");
            return;
        }

        sendSuccessMessage(message, user.getAsMention() + " has been banned for " + DateUtils.formatDateDiff(duration));
        module.banManager.ban(message.getGuild().getId(), user.getId(), message.getAuthor().getId(), duration);
    }
}
