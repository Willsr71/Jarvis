package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;

import java.awt.*;
import java.util.ArrayList;

public class CommandHelp extends Command {
    private Jarvis jarvis;

    public CommandHelp(Jarvis jarvis) {
        super("help", "help", "Displays commands and custom commands", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("Commands", null).setColor(Color.GREEN);

        // Moduleless commands
        embed.addField("No module", getCommandGroupString(jarvis.commandManager.getCommandsByModule(null)), false);

        // All the other modules
        for (String moduleName : jarvis.moduleManager.getModules()) {
            Module module = jarvis.moduleManager.getModule(moduleName);

            if (!module.isEnabled(message.getGuild().getIdLong())) {
                continue;
            }

            embed.addField(module.getName(), getCommandGroupString(jarvis.commandManager.getCommandsByModule(module)), false);
        }

        message.getChannel().sendMessage(embed.build()).queue();
    }

    public String getCommandGroupString(ArrayList<String> commands) {
        int maxLen = 0;

        for (String command : commands) {
            maxLen = Math.max(command.length(), maxLen);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String commandName : commands) {
            Command command = jarvis.commandManager.getCommand(commandName);
            stringBuilder.append('`').append(commandName).append(getFiller(maxLen - commandName.length())).append('`');
            stringBuilder.append(' ').append(command.getUsage());
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }
}
