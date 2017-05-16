package sr.will.jarvis.module.smashbot.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.smashbot.ModuleSmashBot;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandFlair extends Command {
    ModuleSmashBot module;

    private ArrayList<String> ignoredRoles = new ArrayList<>(Arrays.asList("Simulations", "Smash Bros"));

    public CommandFlair(ModuleSmashBot module) {
        super("flair", "flair <setname|setcolor|getcolor> [name|color|user tag]", "Flair command for setting flair name and colors", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        Member member = message.getGuild().getMember(message.getAuthor());

        if (args.length == 0) {
            printHelp(message.getChannel());
            return;
        }

        switch (args[0]) {
            case "setname":
                if (args.length < 2) {
                    sendFailureMessage(message, "You did not specify a name!");
                    return;
                }

                String name = condenseArgs(Arrays.copyOfRange(args, 1, args.length));

                if (ignoredRoles.contains(name)) {
                    sendFailureMessage(message, "That is a reserved name!");
                    return;
                }

                for (Role role : member.getRoles()) {
                    if (ignoredRoles.contains(role.getName())) {
                        continue;
                    }

                    setRoleName(role, name, message);
                    return;
                }

                message.getGuild().getController().createRole().setPermissions(Permission.MESSAGE_READ).queue(role -> {
                    message.getGuild().getController().addRolesToMember(member, role).queue();
                    setRoleName(role, name, message);
                });
                break;

            case "setcolor":
                if (args.length < 2) {
                    sendFailureMessage(message, "You did not specify a color!");
                    return;
                }

                Color color;
                try {
                    color = hex2Rgb(args[1]);
                } catch (StringIndexOutOfBoundsException e) {
                    sendFailureMessage(message, "Invalid hex code. Use the format #00FF00");
                    return;
                }

                for (Role role : member.getRoles()) {
                    if (ignoredRoles.contains(role.getName())) {
                        continue;
                    }

                    setRoleColor(role, color, message);
                    return;
                }

                message.getGuild().getController().createRole().setPermissions(Permission.MESSAGE_READ).queue(role -> {
                    message.getGuild().getController().addRolesToMember(member, role).queue();
                    setRoleColor(role, color, message);
                });
                break;

            case "getcolor":
                for (Role role : member.getRoles()) {
                    if (ignoredRoles.contains(role.getName())) {
                        continue;
                    }

                    getRoleColor(role, message);
                    return;
                }

                message.getGuild().getController().createRole().setPermissions(Permission.MESSAGE_READ).queue(role -> {
                    message.getGuild().getController().addRolesToMember(member, role).queue();
                    getRoleColor(role, message);
                });
                break;

            default:
                printHelp(message.getChannel());
        }
    }

    private void printHelp(MessageChannel channel) {
        channel.sendMessage(new EmbedBuilder().setTitle("Flair Help", null)
                .addField("setname <name>", "Rename your flair to this name", false)
                .addField("setcolor <hex code>", "Change your flair color", false)
                .addField("getcolor", "Get your current color", false)
                .build()).queue();
    }

    public void setRoleName(Role role, String name, Message message) {
        role.getManager().setName(name).queue((x) -> {
            sendSuccessEmote(message);
            //CommandUtils.sendSuccessMessage(message, "Name updated successfully");
        }, (Throwable x) -> {
            sendFailureMessage(message, "Name update failed");
        });
    }

    public void setRoleColor(Role role, Color color, Message message) {
        role.getManager().setColor(color).queue((x) -> {
            sendSuccessEmote(message);
            //CommandUtils.sendSuccessMessage(message, "Color updated successfully");
        }, (Throwable x) -> {
            sendFailureMessage(message, "Color update failed");
        });
    }

    public void getRoleColor(Role role, Message message) {
        sendSuccessMessage(message, String.format("#%02X%02X%02X", role.getColor().getRed(), role.getColor().getGreen(), role.getColor().getBlue()), false);
    }

    private Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf(colorStr.substring(1, 3), 16),
                Integer.valueOf(colorStr.substring(3, 5), 16),
                Integer.valueOf(colorStr.substring(5, 7), 16));
    }
}
