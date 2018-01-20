package sr.will.jarvis.module.assistance.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.assistance.ModuleAssistance;

public class CommandGoogle extends Command {
    private ModuleAssistance module;

    public CommandGoogle(ModuleAssistance module) {
        super("google", "google <search string>", "Displays a lmgtfy.com link of the search string", null);
        this.module = module;
    }

    public void execute(Message message, String... args) {
        String url = "https://lmgtfy.com/?q=";
        for (String arg : args) {
            url += arg + "+";
        }
        url = url.substring(0, url.length() - 1);

        sendSuccessMessage(message, url, false);
    }
}
