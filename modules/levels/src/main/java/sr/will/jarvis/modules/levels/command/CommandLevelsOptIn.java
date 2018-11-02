package sr.will.jarvis.modules.levels.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.levels.ModuleLevels;

public class CommandLevelsOptIn extends Command {
    private ModuleLevels module;

    public CommandLevelsOptIn(ModuleLevels module) {
        super("levelsoptin", "levelsoptin", "Opt into the leveling system", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        long guildId = message.getGuild().getIdLong();
        long userId = message.getAuthor().getIdLong();

        if (!module.userExists(guildId, userId)) {
            module.addUser(guildId, userId, 0);
        }

        if (module.getUserXp(guildId, userId) >= 0) {
            sendFailureMessage(message, "You are already opted into leveling");
            return;
        }

        module.setUserXp(guildId, userId, -module.getUserXp(guildId, userId));
        sendSuccessMessage(message, "You are now opted into leveling");
    }
}
