package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandMute extends Command {
    private Jarvis jarvis;

    public CommandMute(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        message.getChannel().sendMessage("Yup.").queue();
    }
}
