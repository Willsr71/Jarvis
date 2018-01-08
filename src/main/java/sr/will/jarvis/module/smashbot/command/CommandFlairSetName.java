package sr.will.jarvis.module.smashbot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.smashbot.ModuleSmashBot;

public class CommandFlairSetName extends Command {
    private ModuleSmashBot module;

    public CommandFlairSetName(ModuleSmashBot module) {
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
        long guildId = message.getGuild().getIdLong();
        Member member = message.getMember();

        if (module.getIgnoredRoles(guildId).contains(name)) {
            sendFailureMessage(message, "That is a reserved name!");
            return;
        }

        module.setFlairName(member, name);
        sendSuccessEmote(message);
    }
}
