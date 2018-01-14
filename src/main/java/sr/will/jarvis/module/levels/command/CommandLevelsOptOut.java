package sr.will.jarvis.module.levels.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.levels.ModuleLevels;

public class CommandLevelsOptOut extends Command {
    private ModuleLevels module;

    public CommandLevelsOptOut(ModuleLevels module) {
        super("levelsoptout", "levelsoptout", "Opt out of the leveling system", module);
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

        if (module.getUserXp(guildId, userId) < 0) {
            sendFailureMessage(message, "You are already opted out of leveling");
            return;
        }

        module.setUserXp(guildId, userId, -module.getUserXp(guildId, userId));
        sendSuccessMessage(message, "You are now opted out of leveling", false);
    }
}
