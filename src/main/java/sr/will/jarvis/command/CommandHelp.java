package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

import java.awt.*;
import java.util.ArrayList;

public class CommandHelp extends Command {
    private Jarvis jarvis;

    public CommandHelp(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("Commands", "https://jarvis.will.sr").setColor(Color.GREEN);

        String string = "";
        ArrayList<String> commands = jarvis.commandManager.getCommands();
        for (String command : commands) {
            string += command + "\n";
        }

        embed.addField("Commands", string, true);

        string = "";
        ArrayList<String> customCommands = jarvis.commandManager.getCustomCommandsByGuild(message.getGuild().getId());
        for (String command : customCommands) {
            string += command + "\n";
        }

        embed.addField("Custom commands", string, true);

        message.getChannel().sendMessage(embed.build()).queue();
    }


}
