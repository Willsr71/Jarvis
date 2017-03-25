package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.util.CommandUtils;

public class CommandUnmute extends Command {
    private Jarvis jarvis;

    public CommandUnmute(Jarvis jarvis) {
        this.jarvis = jarvis;
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

        if (!jarvis.muteManager.isMuted(message.getGuild().getId(), user.getId())) {
            CommandUtils.sendFailureMessage(message, "User is not muted");
            return;
        }

        CommandUtils.sendSuccessMessage(message, user.getAsMention() + " has been unmuted");
        jarvis.muteManager.unmute(message.getGuild().getId(), user.getId());
    }
}
