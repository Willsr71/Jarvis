package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;

public class CommandBan extends Command {
    private Jarvis jarvis;

    public CommandBan(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.BAN_MEMBERS)) {
            message.getChannel().sendMessage("`You don't have permission for that`").queue();
            return;
        }

        if (message.getMentionedUsers().size() == 0) {
            message.getChannel().sendMessage("`No user tagged`");
            return;
        }

        User user = message.getMentionedUsers().get(0);

        if (user.getId().equals(message.getJDA().getSelfUser().getId())) {
            message.getChannel().sendMessage("`You cannot mute the all powerful JARVIS`").queue();
            return;
        }

        if (jarvis.muteManager.isMuted(user.getId(), message.getGuild().getId())) {
            message.getChannel().sendMessage("`User is already muted`").queue();
            return;
        }
    }
}
