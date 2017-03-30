package sr.will.jarvis.module.admin.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.admin.ModuleAdmin;

public class CommandUnmute extends Command {
    private ModuleAdmin module;

    public CommandUnmute(ModuleAdmin module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkUserPermission(message, Permission.VOICE_MUTE_OTHERS);

        User user = getMentionedUser(message, args);
        if (user == null) {
            sendFailureMessage(message, "No user tagged");
            return;
        }

        if (!module.muteManager.isMuted(message.getGuild().getId(), user.getId())) {
            sendFailureMessage(message, "User is not muted");
            return;
        }

        sendSuccessMessage(message, user.getAsMention() + " has been unmuted");
        module.muteManager.unmute(message.getGuild().getId(), user.getId());
    }
}
