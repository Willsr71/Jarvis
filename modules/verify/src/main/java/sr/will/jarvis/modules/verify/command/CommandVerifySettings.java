package sr.will.jarvis.modules.verify.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.verify.ModuleVerify;

public class CommandVerifySettings extends Command {
    private ModuleVerify module;

    public CommandVerifySettings(ModuleVerify module) {
        super("verifysettings", "verifysettings <channel> <role>", "Set the Verify Module settings", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkBotPermission(message, Permission.NICKNAME_MANAGE);
        checkBotPermission(message, Permission.MANAGE_ROLES);
        checkUserPermission(message, Permission.ADMINISTRATOR);

        if (message.getMentionedChannels().size() == 0 || message.getMentionedRoles().size() == 0) {
            sendUsage(message);
            return;
        }

        MessageChannel channel = message.getMentionedChannels().get(0);
        Role role = message.getMentionedRoles().get(0);

        module.setVerificationData(message.getGuild().getIdLong(), channel.getIdLong(), role.getIdLong());
    }
}
