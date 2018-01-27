package sr.will.jarvis.modules.overwatch.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.overwatch.ModuleOverwatch;

public class CommandBattleTagRemove extends Command {
    private ModuleOverwatch module;

    public CommandBattleTagRemove(ModuleOverwatch module) {
        super("battletagremove", "battletagremove", "Removes the battletag from the sender's discord account", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        String battletag = module.getBattletag(message.getAuthor().getIdLong());
        if (battletag == null) {
            sendFailureMessage(message, "Account is not linked to a battletag");
            return;
        }

        module.removeBattletag(message.getAuthor().getIdLong());
        sendSuccessMessage(message, "Account unlinked from battletag " + battletag);
    }
}
