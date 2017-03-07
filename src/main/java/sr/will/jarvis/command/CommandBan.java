package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;

import java.awt.*;

public class CommandBan extends Command {
    private Jarvis jarvis;

    public CommandBan(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.BAN_MEMBERS)) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error").setColor(Color.RED).setDescription("You don't have permission for that").build()).queue();
            return;
        }

        if (message.getMentionedUsers().size() == 0) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error").setColor(Color.RED).setDescription("No user tagged").build()).queue();
            return;
        }

        User user = message.getMentionedUsers().get(0);

        if (user.getId().equals(message.getJDA().getSelfUser().getId())) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error").setColor(Color.RED).setDescription("You cannot ban the all powerful " + message.getGuild().getMemberById(message.getJDA().getSelfUser().getId()).getEffectiveName()).build()).queue();
            return;
        }

        if (jarvis.muteManager.isMuted(user.getId(), message.getGuild().getId())) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error").setColor(Color.RED).setDescription("User is already banned").build()).queue();
            return;
        }
    }
}
