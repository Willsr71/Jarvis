package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

import java.awt.*;

public class CommandMuteMe extends Command {
    private Jarvis jarvis;

    public CommandMuteMe(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        User user = message.getAuthor();

        if (args.length == 0) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Success", "https://jarvis.will.sr").setColor(Color.GREEN).setDescription(user.getAsMention() + " has been muted").build()).queue();
            jarvis.muteManager.mute(message.getGuild().getId(), user.getId(), message.getAuthor().getId());
            return;
        }

        long duration = 0;

        try {
            duration = DateUtils.parseDateDiff(args[0], true);
        } catch (Exception e) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("Invalid time").build()).queue();
            return;
        }

        message.getChannel().sendMessage(new EmbedBuilder().setTitle("Success", "https://jarvis.will.sr").setColor(Color.GREEN).setDescription(user.getAsMention() + " has been muted for " + DateUtils.formatDateDiff(duration)).build()).queue();
        jarvis.muteManager.mute(message.getGuild().getId(), user.getId(), message.getAuthor().getId(), duration);
    }
}
