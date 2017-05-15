package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

import java.awt.*;

public class CommandModules extends Command {
    private Jarvis jarvis;

    public CommandModules(Jarvis jarvis) {
        super("modules", "modules", "Displays available modules", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        for (String name : jarvis.moduleManager.getModules().keySet()) {
            EmbedBuilder embed = new EmbedBuilder();
            Module module = jarvis.moduleManager.getModule(name);

            embed.setTitle(module.getName(), null);
            embed.setDescription(module.getHelpText());
            if (module.isEnabled(message.getGuild().getIdLong())) {
                embed.setColor(Color.GREEN);
                embed.addField("Disable Command", "!moduledisable " + module.getName().toLowerCase(), true);
            } else {
                embed.setColor(Color.RED);
                embed.addField("Enable Command", "!moduleenable " + module.getName().toLowerCase(), true);
            }

            message.getChannel().sendMessage(embed.build()).queue();
        }
    }
}
