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

    public static String encodeString(String string) {
        StringBuilder builder = new StringBuilder();

        for (char c : string.toCharArray()) {
            if (c > 4095) {
                builder.append(String.format("\\u%X", (int) c));
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    public static String decodeString(String string) {
        StringBuilder builder = new StringBuilder(string);

        while (builder.indexOf("\\u") != -1) {
            int index = builder.indexOf("\\u");
            String hex = builder.substring(index + 2, index + 6);
            int val = Integer.valueOf(hex, 16);

            builder.delete(index, index + 6);
            builder.insert(index, (char) val);
        }

        return builder.toString();
    }
}
