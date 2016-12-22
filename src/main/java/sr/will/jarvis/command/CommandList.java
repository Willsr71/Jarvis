package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

import java.util.ArrayList;

public class CommandList extends Command {
    private Jarvis jarvis;

    public CommandList(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        String string = "Commands:```\n";
        ArrayList<String> commands = jarvis.commandManager.getCommands();
        for (String command : commands) {
            string += command + "\n";
        }

        string += "```\nCustom commands:```\n";
        ArrayList<String> customCommands = jarvis.commandManager.getCustomCommandsByGuild(message.getGuild().getId());
        for (String command : customCommands) {
            string += command + "\n";
        }
        string += "```";

        message.getChannel().sendMessage(string).queue();
    }
}
