package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

public class CommandMute extends Command {
    private Jarvis jarvis;

    public CommandMute(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            message.getChannel().sendMessage("`You don't have permission for that`").queue();
            return;
        }

        if (message.getMentionedUsers().size() == 0) {
            message.getChannel().sendMessage("`No user tagged`");
            return;
        }

        User user = message.getMentionedUsers().get(0);

        if (user.getId().equals("182630837412560896")) {
            message.getChannel().sendMessage("`You cannot mute the all powerful JARVIS`").queue();
            return;
        }

        if (jarvis.muteManager.isMuted(user.getId(), message.getGuild().getId())) {
            message.getChannel().sendMessage("`User is already muted`").queue();
            return;
        }

        if (args.length == 1) {
            jarvis.muteManager.mute(user.getId(), message.getAuthor().getId(), message.getGuild().getId());
            message.getChannel().sendMessage("`" + user.getAsMention() + " has been muted`").queue();
            return;
        }

        long duration = 0;

        try {
            duration = DateUtils.parseDateDiff(args[1], true);
        } catch (Exception e) {
            message.getChannel().sendMessage("`Error parsing time`").queue();
            return;
        }

        message.getChannel().sendMessage("`" + user.getAsMention() + " has been muted for " + DateUtils.formatDateDiff(duration) + "`").queue();
        jarvis.muteManager.mute(user.getId(), message.getAuthor().getId(), message.getGuild().getId(), duration);
    }
}
