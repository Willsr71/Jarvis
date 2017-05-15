package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandActuallyGoogle extends Command {
    private Jarvis jarvis;

    public CommandActuallyGoogle(Jarvis jarvis) {
        super("actuallygoogle", "actuallygoogle <search string>", "Displays a google.com link of the search string", null);
        this.jarvis = jarvis;
    }

    public void execute(Message message, String... args) {
        String url = "https://google.com/?q=";
        for (String arg : args) {
            url += arg + "+";
        }
        url = url.substring(0, url.length() - 1);

        sendSuccessMessage(message, url, false);
    }
}
