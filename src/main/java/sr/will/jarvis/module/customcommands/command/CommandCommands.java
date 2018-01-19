package sr.will.jarvis.module.customcommands.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.customcommands.ModuleCustomCommands;

import java.awt.*;
import java.util.ArrayList;

public class CommandCommands extends Command {
    private ModuleCustomCommands module;

    public CommandCommands(ModuleCustomCommands module) {
        super("commands", "commands", "Displays custom commands", null);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        EmbedBuilder embed = new EmbedBuilder().setTitle("Custom Commands", null).setColor(Color.GREEN);

        embed.setDescription(getCustomCommandGroupString(message.getGuild().getIdLong(), module.getCustomCommandsByGuild(message.getGuild().getIdLong())));

        message.getChannel().sendMessage(embed.build()).queue();
    }

    public String getCustomCommandGroupString(long guildId, ArrayList<String> commands) {
        int maxLen = 0;

        for (String command : commands) {
            maxLen = Math.max(command.length(), maxLen);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String commandName : commands) {
            String commandResult = module.getCustomCommandResponse(guildId, commandName);
            stringBuilder.append('`').append(commandName).append(getFiller(maxLen - commandName.length())).append('`');
            //stringBuilder.append(' ').append(commandResult);
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }
}
