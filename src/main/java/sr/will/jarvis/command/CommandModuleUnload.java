package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

public class CommandModuleUnload extends Command {
    private Jarvis jarvis;

    public CommandModuleUnload(Jarvis jarvis) {
        super("moduleunload", "moduleunload <module>", "Unloads a module", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        // Only allow the bot owner to load jars
        if (!jarvis.config.discord.owners.contains(message.getAuthor().getId())) {
            sendFailureMessage(message, "You don't have permission for that");
            return;
        }

        if (args.length != 1) {
            sendFailureMessage(message, "Usage: !moduleunload <module>");
            return;
        }

        Module module = jarvis.moduleManager.getModule(args[0]);

        if (module == null) {
            sendFailureMessage(message, "Module is not loaded");
            return;
        }

        try {
            String name = module.getDescription().getName();

            jarvis.moduleManager.unloadModule(module);
            sendSuccessMessage(message, "Unloaded plugin " + name);
        } catch (Exception e) {
            sendFailureMessage(message, "Failed to unload plugin:\n" + e.toString());
        }
    }
}
