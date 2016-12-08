package sr.will.jarvis.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;

public class CommandMute extends Command {
    private Jarvis jarvis;

    public CommandMute(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (!message.getGuild().getMemberById(message.getAuthor().getId()).hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            message.getChannel().sendMessage("You don't have permission for that.").queue();
            return;
        }

        for (User user : message.getMentionedUsers()) {
            mute(user, message.getAuthor(), message.getGuild(), message.getChannel());
        }
    }

    public void mute(User user, User invoker, Guild guild, MessageChannel channel) {
        if (jarvis.muteManager.isMuted(user.getId(), guild.getId())) {
            channel.sendMessage("User is already muted.").queue();
            return;
        }

        jarvis.muteManager.mute(user.getId(), invoker.getId(), guild.getId());
        channel.sendMessage(user.getAsMention() + " has been muted.").queue();
    }
}
