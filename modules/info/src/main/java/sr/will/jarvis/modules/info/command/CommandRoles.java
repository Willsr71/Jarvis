package sr.will.jarvis.modules.info.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.info.ModuleInfo;

import java.awt.*;
import java.util.List;

public class CommandRoles extends Command {
    private ModuleInfo module;

    public CommandRoles(ModuleInfo module) {
        super("roles", "roles", "Get roles of a guild", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        Guild guild = message.getGuild();

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        embed.setAuthor(guild.getName() + " (" + guild.getIdLong() + ")", null, guild.getIconUrl());
        embed.addField("**> Roles**", getRoleString(guild.getRoles()), true);

        message.getChannel().sendMessage(embed.build()).queue();
    }

    private String getRoleString(List<Role> roles) {
        int maxLen = 0;

        for (Role role : roles) {
            maxLen = Math.max(role.getId().length(), maxLen);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Role role : roles) {
            stringBuilder.append('`').append(role.getId()).append(getFiller(maxLen - role.getId().length())).append('`');
            stringBuilder.append(' ').append(role.getName());
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }
}
