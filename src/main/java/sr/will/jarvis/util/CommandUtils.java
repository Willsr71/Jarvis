package sr.will.jarvis.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.Jarvis;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandUtils {
    public static User getMentionedUser(Message message, String... args) {
        if (message.getMentionedUsers().size() != 0) {
            return message.getMentionedUsers().get(0);
        }

        try {
            return Jarvis.getInstance().getJda().getUserById(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
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

    public static void sendSuccessEmote(Message message) {
        message.addReaction("\uD83D\uDC4C").queue();
    }

    public static void sendSuccessMessage(TextChannel channel, String string, boolean delete, Message... messagesToDelete) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Success!", "https://jarvis.will.sr")
                .setColor(Color.GREEN)
                .setDescription(string);

        if (delete) {
            channel.sendMessage(embed.build()).queue(success -> {
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }

                    ArrayList<Message> messages = new ArrayList<>(Arrays.asList(messagesToDelete));
                    messages.add(success);

                    if (messages.size() == 1) {
                        messages.get(0).delete().queue();
                    } else {
                        channel.deleteMessages(messages).queue();
                    }
                }).start();
            });
        } else {
            channel.sendMessage(embed.build()).queue();
        }
    }

    public static void sendSuccessMessage(TextChannel channel, String string, Message... messagesToDelete) {
        sendSuccessMessage(channel, string, true, messagesToDelete);
    }

    public static void sendSuccessMessage(Message message, String string, boolean delete) {
        sendSuccessMessage(message.getTextChannel(), string, delete, message);
    }

    public static void sendSuccessMessage(Message message, String string) {
        sendSuccessMessage(message, string, true);
    }

    public static void sendFailureMessage(Message message, String string) {
        message.getChannel().sendMessage(new EmbedBuilder()
                .setTitle("Error", "https://jarvis.will.sr")
                .setColor(Color.RED)
                .setDescription(string)
                .build()
        ).queue();
    }
}
