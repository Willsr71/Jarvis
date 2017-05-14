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

        // Moduleless commands

        ArrayList<String> commands = jarvis.commandManager.getCommands();


        embed.setDescription(getCommandGroupString(jarvis.commandManager.getCommands()));

        message.getChannel().sendMessage(embed.build()).queue();
    }

    @Override
    public String getUsage() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays commands and custom commands";
    }

    @Override
    public boolean isModuleEnabled(long guildId) {
        return true;
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

        System.out.println(stringBuilder.length());
        return stringBuilder.toString();
    }

    public String getFiller(int len) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int x = 0; x < len + 1; x += 1) {
            stringBuilder.append(".");
        }

        return stringBuilder.toString();
    }
}
