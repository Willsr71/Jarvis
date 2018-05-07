package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;

public class CommandSource extends Command {

    public CommandSource() {
        super("source", "source", "Displays a link to the GitHub repository", null);
    }

    @Override
    public void execute(Message message, String... args) {
        sendSuccessMessage(message, "https://github.com/Willsr71/Jarvis");
    }
}
