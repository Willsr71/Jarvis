package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandGoogle extends Command {
    private Jarvis jarvis;

    public CommandGoogle(Jarvis jarvis) {
        super("google", "google <search string>", "Displays a lmgtfy.com link of the search string", null);
        this.jarvis = jarvis;
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
