package sr.will.jarvis.modules.admin.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.admin.ModuleAdmin;

public class CommandBan extends Command {
    private ModuleAdmin module;

    public CommandBan(ModuleAdmin module) {
        super("ban", "ban <user mention|user id> [duration]", "Bans the specified member for the specified amount of time. Default time is infinite", module);
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

        if (user.getId().equals(message.getAuthor().getId())) {
            sendFailureMessage(message, "You cannot ban yourself");
            return;
        }

        if (module.banManager.isBanned(message.getGuild().getIdLong(), user.getIdLong())) {
            sendFailureMessage(message, "User already banned");
            return;
        }

        if (args.length == 1) {
            sendSuccessEmote(message);
            module.banManager.ban(message.getGuild().getIdLong(), user.getIdLong(), message.getAuthor().getIdLong());
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
        module.banManager.ban(message.getGuild().getIdLong(), user.getIdLong(), message.getAuthor().getIdLong(), duration);
    }
}
