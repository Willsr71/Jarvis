package sr.will.jarvis.module.flair.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.flair.ModuleFlair;

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

        module.setFlairName(member, name);
        sendSuccessEmote(message);
    }
}
