package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

import java.awt.*;

public class CommandModules extends Command {
    private Jarvis jarvis;

    public CommandModules(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("Modules", null).setColor(Color.GREEN);

        for (String name : jarvis.moduleManager.getModules().keySet()) {
            Module module = jarvis.moduleManager.getModule(name);
            embed.addField(module.getName(), module.getHelpText(), true);
            embed.addField("Enabled", module.isEnabled(message.getGuild().getId()) ? "Yes" : "No", true);
        }

        embed.addField("Dev", "A few useful development commands", false);

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
