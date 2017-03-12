package sr.will.jarvis.util;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;

public class CommandUtils {
    public static User getMentionedUser(Message message, String... args) {
        if (message.getMentionedUsers().size() != 0) {
            return message.getMentionedUsers().get(0);
        }

        return Jarvis.getInstance().getJda().getUserById(args[0]);
    }
}
