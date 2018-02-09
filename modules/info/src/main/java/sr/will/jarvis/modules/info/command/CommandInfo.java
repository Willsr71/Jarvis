package sr.will.jarvis.modules.info.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.info.ModuleInfo;

import java.awt.*;

public class CommandInfo extends Command {
    private ModuleInfo module;

    public CommandInfo(ModuleInfo module) {
        super("info", "info [user mention]", "Info of a user", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        User user = getMentionedUser(message, args);
        if (user == null) {
            user = message.getAuthor();
        }
        Member member = message.getGuild().getMember(user);

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        embed.setAuthor(user.getName() + "#" + user.getDiscriminator() + " (" + user.getIdLong() + ")", null, user.getEffectiveAvatarUrl());
        embed.setThumbnail(user.getEffectiveAvatarUrl());
        embed.addField("**> User Information**",
                "\n> Account Creation: " + user.getCreationTime().format(module.dateTimeFormatter) +
                        "\n> Status: " + member.getOnlineStatus().name() +
                        "\n> Playing: " + member.getGame().getName(),
                true);
        embed.addField("**> Member Information**",
                "\n> Joined Guild: " + member.getJoinDate().format(module.dateTimeFormatter) +
                        "\n> Nickname: " + member.getNickname(), true);
        embed.addField("**> Roles**", getRolesAsString(member.getRoles()), true);
        embed.addField("**> Seen On**", getGuildsAsString(user.getMutualGuilds()), true);

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
