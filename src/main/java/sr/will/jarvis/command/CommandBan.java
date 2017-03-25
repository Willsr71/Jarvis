package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.util.CommandUtils;

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

        User user = CommandUtils.getMentionedUser(message, args);
        if (user == null) {
            CommandUtils.sendFailureMessage(message, "No user tagged");
            return;
        }

        if (user.getId().equals(message.getJDA().getSelfUser().getId())) {
            CommandUtils.sendFailureMessage(message, "You cannot ban the all powerful " + message.getGuild().getMember(message.getJDA().getSelfUser()).getEffectiveName());
            return;
        }

        if (jarvis.banManager.isBanned(message.getGuild().getId(), user.getId())) {
            CommandUtils.sendFailureMessage(message, "User already banned");
            return;
        }

        if (args.length == 1) {
            CommandUtils.sendSuccessEmote(message);
            jarvis.banManager.ban(message.getGuild().getId(), user.getId(), message.getAuthor().getId());
            return;
        }

        long duration = 0;

        try {
            duration = DateUtils.parseDateDiff(args[1], true);
        } catch (Exception e) {
            CommandUtils.sendFailureMessage(message, "Invalid time");
            return;
        }

        CommandUtils.sendSuccessMessage(message, user.getAsMention() + " has been banned for " + DateUtils.formatDateDiff(duration));
        jarvis.banManager.ban(message.getGuild().getId(), user.getId(), message.getAuthor().getId(), duration);
    }
}
