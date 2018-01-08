package sr.will.jarvis.module.smashbot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.smashbot.ModuleSmashBot;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandFlairImport extends Command {
    private ModuleSmashBot module;

    public CommandFlairImport(ModuleSmashBot module) {
        super("flairimport", "flairimport [ignored roles]", "Import flairs", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.ADMINISTRATOR);
        checkBotPermission(message, Permission.MANAGE_ROLES);

        long guildId = message.getGuild().getIdLong();
        ArrayList<String> ignoredRoles = new ArrayList<>(Arrays.asList(args));

        for (Member member : message.getGuild().getMembers()) {
            for (Role role : member.getRoles()) {
                if (ignoredRoles.contains(role.getName())) {
                    continue;
                }

                message.getChannel().sendMessage("Importing user '" + member.getUser().getName() + "' role '" + role.getName() + "'").queue();

                String color = "";
                if (role.getColor() == null) {
                    color = "#FFFFFF";
                } else {
                    color = module.getHexFromColor(role.getColor());
                }

                Jarvis.getDatabase().execute("INSERT INTO flairs (guild, user, role, name, color) VALUES (?, ?, ?, ?, ?);",
                        guildId, member.getUser().getIdLong(), role.getIdLong(), role.getName(), color);
            }
        }

        sendSuccessMessage(message, "Imported all roles", false);
    }
}
