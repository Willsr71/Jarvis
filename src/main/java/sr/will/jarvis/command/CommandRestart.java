package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;

public class CommandRestart extends Command {
    private Jarvis jarvis;

    public CommandRestart(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        // Only allow the bot owner to restart the bot
        if (!jarvis.config.discord.owners.contains(message.getAuthor().getId())) {
            sendFailureMessage(message, "You don't have permission for that");
            return;
        }

        for (User user : message.getMentionedUsers()) {
            if (user.getId().equals(message.getJDA().getSelfUser().getId())) {
                sendSuccessEmote(message);
                jarvis.stop();
                return;
            }
        }
    }
}
