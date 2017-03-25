package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.util.CommandUtils;

public class CommandEmoji extends Command {
    private Jarvis jarvis;

    public CommandEmoji(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        CommandUtils.sendSuccessMessage(message, CommandUtils.encodeString(message.getRawContent()), false);
    }
}
