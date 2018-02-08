package sr.will.jarvis.modules.info.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.info.ModuleInfo;

import java.awt.*;

public class CommandGuild extends Command {
    private ModuleInfo module;

    public CommandGuild(ModuleInfo module) {
        super("guild", "guild", "Info of a guild", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        Guild guild = message.getGuild();

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        embed.setAuthor(guild.getName() + " (" + guild.getIdLong() + ")", null, guild.getIconUrl());
        embed.setThumbnail(guild.getIconUrl());
        embed.addField("**> Guild Information**",
                "\n> Guild Creation: " + guild.getCreationTime().format(module.dateTimeFormatter) +
                        "\n> Owner: " + guild.getOwner().getAsMention() +
                        "\n> Region: " + guild.getRegion().getName() +
                        "\n> Default Channel: " + guild.getDefaultChannel().getAsMention() +
                        "\n> Users: " + guild.getMembers().size() +
                        "\n> Roles: " + guild.getRoles().size() +
                        "\n> Text Channels: " + guild.getTextChannels().size() +
                        "\n> Voice Channels: " + guild.getVoiceChannels().size() +
                        "\n> Emotes: " + guild.getEmotes().size(),
                true);

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
