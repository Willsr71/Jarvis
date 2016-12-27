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
        if (!message.getAuthor().getId().equals(jarvis.config.discord.owner)) {
            message.getChannel().sendMessage("`No permission`").queue();
            return;
        }

        for (User user : message.getMentionedUsers()) {
            if (user.getId().equals(message.getJDA().getSelfUser().getId())) {
                message.getChannel().sendMessage("`Restarting...`").queue();
                jarvis.stop();
                return;
            }
        }
    }
}
