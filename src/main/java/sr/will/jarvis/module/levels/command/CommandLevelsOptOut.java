package sr.will.jarvis.module.levels.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.levels.ModuleLevels;

public class CommandLevelsOptOut extends Command {
    private ModuleLevels module;

    public CommandLevelsOptOut(ModuleLevels module) {
        super("levelsoptout", "levelsoptout", "Opt out of the leveling system. Note: This will reset your xp", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        long guildId = message.getGuild().getIdLong();
        long userId = message.getAuthor().getIdLong();

        if (!module.userExists(guildId, userId)) {
            module.addUser(guildId, userId);
        }

        if (module.getUserXp(guildId, userId) == -1) {
            sendFailureMessage(message, "You are already opted out of leveling");
            return;
        }

        module.setUserXp(guildId, userId, -1);
        sendSuccessMessage(message, "You are now opted out of leveling", false);
    }
}
