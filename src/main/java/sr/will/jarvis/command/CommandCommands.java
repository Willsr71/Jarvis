package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

import java.awt.*;
import java.util.ArrayList;

public class CommandCommands extends Command {
    private Jarvis jarvis;

    public CommandCommands(Jarvis jarvis) {
        super("commands", "commands", "Displays custom commands", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("Custom Commands", null).setColor(Color.GREEN);

        embed.setDescription(getCustomCommandGroupString(message.getGuild().getIdLong(), jarvis.commandManager.getCustomCommandsByGuild(message.getGuild().getIdLong())));

        message.getChannel().sendMessage(embed.build()).queue();
    }

    public static String getCustomCommandGroupString(long guildId, ArrayList<String> commands) {
        int maxLen = 0;

        for (String command : commands) {
            maxLen = Math.max(command.length(), maxLen);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String commandName : commands) {
            String commandResult = Jarvis.getInstance().commandManager.getCustomCommandResponse(guildId, commandName);
            stringBuilder.append('`').append(commandName).append(getFiller(maxLen - commandName.length())).append('`');
            stringBuilder.append(' ').append(commandResult);
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }
}
