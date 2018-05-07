package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.ModuleDescription;

import java.io.File;

public class CommandModuleLoad extends Command {
    private Jarvis jarvis;

    public CommandModuleLoad(Jarvis jarvis) {
        super("moduleload", "moduleload <jarfile>", "Loads a module jar", null);
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
            sendFailureMessage(message, "Usage: !moduleload <jarfile>");
            return;
        }

        File file = new File("modules/" + args[0]);

        if (jarvis.moduleManager.isModuleLoaded(file)) {
            sendFailureMessage(message, "Module is already loaded");
            return;
        }

        try {
            ModuleDescription description = jarvis.moduleManager.getModuleDescription(file);

            if (jarvis.moduleManager.isModuleLoaded(description.getName())) {
                sendFailureMessage(message, "Module is already loaded");
                return;
            }

            jarvis.moduleManager.loadModule(description);
            sendSuccessMessage(message, "Loaded plugin " + description.getName() + " version " + description.getVersion() + " by " + description.getAuthor());
        } catch (Exception e) {
            sendFailureMessage(message, "Failed to load plugin:\n" + e.toString());
        }
    }
}
