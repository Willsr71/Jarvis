package sr.will.jarvis.modules.flair.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.flair.ModuleFlair;

public class CommandFlairSetName extends Command {
    private ModuleFlair module;

    public CommandFlairSetName(ModuleFlair module) {
        super("flairsetname", "flairsetname <name>", "Rename your flair to this name", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkBotPermission(message, Permission.MANAGE_ROLES);

        if (args.length < 1) {
            sendFailureMessage(message, "You did not specify a name!");
            return;
        }

        String name = condenseArgs(args);
        Member member = message.getMember();

        if (name.length() == 0 || name.length() > 32) {
            sendFailureMessage(message, "Flair must be between 1 and 32 characters (That was " + name.length() + ")");
            return;
        }

        module.setFlairName(member, name);
        sendSuccessEmote(message);
    }
}
