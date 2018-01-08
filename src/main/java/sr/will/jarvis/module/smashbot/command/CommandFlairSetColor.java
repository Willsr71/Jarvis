package sr.will.jarvis.module.smashbot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.smashbot.ModuleSmashBot;

import java.awt.*;

public class CommandFlairSetColor extends Command {
    private ModuleSmashBot module;

    public CommandFlairSetColor(ModuleSmashBot module) {
        super("flairsetcolor", "flairsetcolor <color|hex code>", "Change your flair color", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkBotPermission(message, Permission.MANAGE_ROLES);

        if (args.length < 1) {
            sendFailureMessage(message, "You did not specify a color!");
            return;
        }

        long guildId = message.getGuild().getIdLong();
        Member member = message.getMember();

        Color color = module.getColorFromHex(args[0]);
        if (color == null) {
            sendFailureMessage(message, "Invalid color. Use a hex code or a valid color name");
            return;
        }

        module.setFlairColor(member, color);
        sendSuccessEmote(message);
    }
}
