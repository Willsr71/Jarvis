package sr.will.jarvis.module.overwatch.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.overwatch.ModuleOverwatch;

public class CommandAddBattleTag extends Command {
    private ModuleOverwatch module;

    public CommandAddBattleTag(ModuleOverwatch module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
    }
}
