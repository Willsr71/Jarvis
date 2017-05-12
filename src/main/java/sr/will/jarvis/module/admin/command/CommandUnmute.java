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
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.VOICE_MUTE_OTHERS);

        User user = getMentionedUser(message, args);
        if (user == null) {
            sendFailureMessage(message, "No user tagged");
            return;
        }

        if (!module.muteManager.isMuted(message.getGuild().getIdLong(), user.getIdLong())) {
            sendFailureMessage(message, "User is not muted");
            return;
        }

        sendSuccessMessage(message, user.getAsMention() + " has been unmuted");
        module.muteManager.unmute(message.getGuild().getIdLong(), user.getIdLong());
    }

    @Override
    public String getUsage() {
        return "unmute <user mention|user id>";
    }

    @Override
    public String getDescription() {
        return "Unmutes the specified member";
    }

    @Override
    public boolean getModuleEnabled(long guildId) {
        return module.isEnabled(guildId);
    }
}
