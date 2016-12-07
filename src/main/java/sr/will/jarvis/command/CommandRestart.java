package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandRestart extends Command {
    private Jarvis jarvis;

    public CommandRestart(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getAuthor().getId().equals("112587845968912384")) { // Willsr71
            message.getChannel().sendMessage("No permission").queue();
            return;
        }

        message.getChannel().sendMessage("Restarting...").queue();
        jarvis.stop();
    }
}
