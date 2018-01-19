package sr.will.jarvis.module.flair.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.flair.ModuleFlair;

import java.awt.*;

public class CommandFlair extends Command {
    private ModuleFlair module;


    public CommandFlair(ModuleFlair module) {
        super("flair", "flair", "Flair help command", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        message.getChannel().sendMessage(new EmbedBuilder().setTitle("Flair Help", null).setColor(Color.GREEN)
                .addField("flairsetname <name>", "Rename your flair to this name", false)
                .addField("flairsetcolor <color|hex code>", "Change your flair color", false)
                .addField("flairgetcolor [user mention]", "Get the color of a user's flair", false)
                .addField("flairlist", "List all flairs and their owners", false)
                .build()).queue();
    }
}
