package sr.will.jarvis.module.admin.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.admin.ModuleAdmin;
import sr.will.jarvis.util.CommandUtils;

public class CommandMute extends Command {
    private ModuleAdmin module;

    public CommandMute(ModuleAdmin module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            CommandUtils.sendFailureMessage(message, "You don't have permission for that");
            return;
        }

        User user = CommandUtils.getMentionedUser(message, args);
        if (user == null) {
            CommandUtils.sendFailureMessage(message, "No user tagged");
            return;
        }

        if (user.getId().equals(message.getJDA().getSelfUser().getId())) {
            CommandUtils.sendFailureMessage(message, "You cannot mute the all powerful " + message.getGuild().getMember(message.getJDA().getSelfUser()).getEffectiveName());
            return;
        }

        if (module.muteManager.isMuted(message.getGuild().getId(), user.getId())) {
            CommandUtils.sendFailureMessage(message, "User already muted");
            return;
        }

        if (args.length == 1) {
            CommandUtils.sendSuccessEmote(message);
            module.muteManager.mute(message.getGuild().getId(), user.getId(), message.getAuthor().getId());
            return;
        }

        long duration = 0;

        try {
            duration = DateUtils.parseDateDiff(args[1], true);
        } catch (Exception e) {
            CommandUtils.sendFailureMessage(message, "Invalid time");
            return;
        }

        CommandUtils.sendSuccessMessage(message, user.getAsMention() + " has been muted for " + DateUtils.formatDateDiff(duration));
        module.muteManager.mute(message.getGuild().getId(), user.getId(), message.getAuthor().getId(), duration);
    }
}
