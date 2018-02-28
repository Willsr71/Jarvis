package sr.will.jarvis.modules.assistance.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.assistance.ModuleAssistance;

public class CommandRegex extends Command {
    private ModuleAssistance module;

    public CommandRegex(ModuleAssistance module) {
        super("regex", "regex <find> <replace> <text>", "Find and replace text", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        if (args.length < 3) {
            sendUsage(message);
            return;
        }

        String find = args[0];
        String replace = args[1];
        String text = condenseArgs(args, 2);

        String result = text.replaceAll(find, replace);

        message.getChannel().sendMessage("```" + result + "```").queue();
    }
}
