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
        super("help", "help [module]", "Displays commands and custom commands", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (args.length == 0) {
            sendModules(message);
        } else {
            sendModule(message, args[0]);
        }
    }

    private void sendModules(Message message) {
        StringBuilder moduleString = new StringBuilder();
        for (String name : jarvis.moduleManager.getModules()) {
            Module module = jarvis.moduleManager.getModule(name);

            if (!module.isEnabled(message.getGuild().getIdLong())) {
                continue;
            }

            moduleString.append("\n").append(module.getDescription().getName());
        }

        // Moduleless commands
        message.getChannel().sendMessage(
                new EmbedBuilder()
                        .setTitle("No Module", null)
                        .setColor(Color.GREEN)
                        .setDescription(getCommandGroupString(jarvis.commandManager.getCommandsByModule(null)))
                        .build()).queue();

        moduleString.append("\n\nUse `!help [module name]` to get commands for that module");
        message.getChannel().sendMessage(
                new EmbedBuilder()
                        .setTitle("Enabled Modules")
                        .setColor(Color.GREEN)
                        .setDescription(moduleString.toString())
                        .build()).queue();
    }

    private void sendModule(Message message, String name) {
        Module module = jarvis.moduleManager.getModule(name);

        if (module == null) {
            sendFailureMessage(message, "Module does not exist");
            return;
        }

        ArrayList<String> moduleCommands = jarvis.commandManager.getCommandsByModule(module);

        if (moduleCommands.size() == 0) {
            sendSuccessMessage(message, "Module does not have any commands", false);
            return;
        }

        message.getChannel().sendMessage(
                new EmbedBuilder()
                        .setTitle(module.getDescription().getName(), null)
                        .setColor(Color.GREEN)
                        .setDescription(getCommandGroupString(moduleCommands))
                        .build()).queue();
    }

    private String getCommandGroupString(ArrayList<String> commands) {
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
