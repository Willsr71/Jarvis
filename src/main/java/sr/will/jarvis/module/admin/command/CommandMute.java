package sr.will.jarvis.module.admin.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.admin.ModuleAdmin;

public class CommandMute extends Command {
    private ModuleAdmin module;

    public CommandMute(ModuleAdmin module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkBotPermission(message, Permission.MANAGE_ROLES);
        checkBotPermission(message, Permission.MANAGE_CHANNEL);
        checkBotPermission(message, Permission.MESSAGE_MANAGE);
        checkUserPermission(message, Permission.VOICE_MUTE_OTHERS);

        User user = getMentionedUser(message, args);
        if (user == null) {
            sendFailureMessage(message, "No user tagged");
            return;
        }

        if (user.getId().equals(message.getJDA().getSelfUser().getId())) {
            sendFailureMessage(message, "You cannot mute the all powerful " + message.getGuild().getMember(message.getJDA().getSelfUser()).getEffectiveName());
            return;
        }

        if (module.muteManager.isMuted(message.getGuild().getId(), user.getId())) {
            sendFailureMessage(message, "User already muted");
            return;
        }

        if (args.length == 1) {
            sendSuccessEmote(message);
            module.muteManager.mute(message.getGuild().getId(), user.getId(), message.getAuthor().getId());
            return;
        }

        long duration = 0;

        try {
            duration = DateUtils.parseDateDiff(args[1], true);
        } catch (Exception e) {
            sendFailureMessage(message, "Invalid time");
            return;
        }

        sendSuccessMessage(message, user.getAsMention() + " has been muted for " + DateUtils.formatDateDiff(duration));
        module.muteManager.mute(message.getGuild().getId(), user.getId(), message.getAuthor().getId(), duration);
    }
}
