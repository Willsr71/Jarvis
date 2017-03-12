package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.util.CommandUtils;

import java.awt.*;

public class CommandUnmute extends Command {
    private Jarvis jarvis;

    public CommandUnmute(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("You don't have permission for that").build()).queue();
            return;
        }

        User user = CommandUtils.getMentionedUser(message, args);
        if (user == null) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("No user tagged").build()).queue();
            return;
        }

        if (!jarvis.muteManager.isMuted(message.getGuild().getId(), user.getId())) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("User is not muted").build()).queue();
            return;
        }

        message.getChannel().sendMessage(new EmbedBuilder().setTitle("Success", "https://jarvis.will.sr").setColor(Color.GREEN).setDescription(user.getAsMention() + " has been unmuted").build()).queue();
        jarvis.muteManager.unmute(message.getGuild().getId(), user.getId());
    }
}
