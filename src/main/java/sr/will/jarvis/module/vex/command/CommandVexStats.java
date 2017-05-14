package sr.will.jarvis.module.vex.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.vex.ModuleVex;

public class CommandVexStats extends Command {
    private ModuleVex module;

    public CommandVexStats(ModuleVex module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        sendSuccessMessage(message, "Not implemented yet");
    }

    @Override
    public String getUsage() {
        return "vexstats";
    }

    @Override
    public String getDescription() {
        return "Display stats for a vex team";
    }

    @Override
    public boolean isModuleEnabled(long guildId) {
        return module.isEnabled(guildId);
    }
}
