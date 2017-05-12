package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;

public class CommandSource extends Command {
    @Override
    public void execute(Message message, String... args) {
        sendSuccessMessage(message, "https://github.com/Willsr71/Jarvis", false);
    }

    @Override
    public String getUsage() {
        return "source";
    }

    @Override
    public String getDescription() {
        return "Displays a link to the GitHub repository";
    }

    @Override
    public boolean getModuleEnabled(long guildId) {
        return true;
    }
}
