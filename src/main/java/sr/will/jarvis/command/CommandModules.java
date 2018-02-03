package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

import java.awt.*;
import java.util.ArrayList;

public class CommandModules extends Command {
    private Jarvis jarvis;

    public CommandModules(Jarvis jarvis) {
        super("modules", "modules", "Displays available modules", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("Modules", null).setColor(Color.GREEN);
        int maxLen = 0;

        ArrayList<String> modules = jarvis.moduleManager.getModules();

        for (String module : modules) {
            maxLen = Math.max(module.length(), maxLen);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String moduleName : modules) {
            Module module = jarvis.moduleManager.getModule(moduleName);

            if (!module.isGuildWhitelisted(message.getGuild().getIdLong())) {
                continue;
            }

            stringBuilder.append('`').append(module.getDescription().getName()).append(getFiller(maxLen - module.getDescription().getName().length())).append('`');
            stringBuilder.append(' ');
            stringBuilder.append('`').append((module.isEnabled(message.getGuild().getIdLong()) ? "Enabled.." : "Disabled.")).append('`');
            stringBuilder.append(' ');
            stringBuilder.append(module.getDescription().getDescription());
            stringBuilder.append('\n');
        }

        embed.setDescription(stringBuilder.toString());
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
