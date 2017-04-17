package sr.will.jarvis.module.overwatch.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.overwatch.ModuleOverwatch;

public class CommandBattleTagRemove extends Command {
    private ModuleOverwatch module;

    public CommandBattleTagRemove(ModuleOverwatch module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        String battletag = module.getBattletag(message.getAuthor().getId());
        if (battletag == null) {
            sendFailureMessage(message, "Account is not linked to a battletag");
            return;
        }

        module.removeBattletag(message.getAuthor().getId());
        sendSuccessMessage(message, "Account unlinked from battletag " + battletag);
    }
}
