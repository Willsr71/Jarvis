package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
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
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("You don't have permission for that").build()).queue();
            return;
        }

        if (message.getMentionedUsers().size() == 0) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("No user tagged").build()).queue();
            return;
        }

        User user = message.getMentionedUsers().get(0);

        if (user.getId().equals(message.getJDA().getSelfUser().getId())) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("You cannot ban the all powerful " + message.getGuild().getMemberById(message.getJDA().getSelfUser().getId()).getEffectiveName()).build()).queue();
            return;
        }

        if (jarvis.banManager.isBanned(message.getGuild().getId(), user.getId())) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("User already banned").build()).queue();
            return;
        }

        if (args.length == 1) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Success", "https://jarvis.will.sr").setColor(Color.GREEN).setDescription(user.getAsMention() + " has been banned").build()).queue();
            jarvis.banManager.ban(message.getGuild().getId(), user.getId(), message.getAuthor().getId());
            return;
        }

        long duration = 0;

        try {
            duration = DateUtils.parseDateDiff(args[1], true);
        } catch (Exception e) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("Invalid time").build()).queue();
            return;
        }

        message.getChannel().sendMessage(new EmbedBuilder().setTitle("Success", "https://jarvis.will.sr").setColor(Color.GREEN).setDescription(user.getAsMention() + " has been banned for " + DateUtils.formatDateDiff(duration)).build()).queue();
        jarvis.banManager.ban(message.getGuild().getId(), user.getId(), message.getAuthor().getId(), duration);
    }
}
