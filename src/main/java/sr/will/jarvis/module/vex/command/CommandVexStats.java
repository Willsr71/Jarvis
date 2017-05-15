package sr.will.jarvis.module.vex.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.vex.ModuleVex;

public class CommandVexStats extends Command {
    private ModuleVex module;

    public CommandVexStats(ModuleVex module) {
        super("vexstats", "vexstats", "Display stats for a vex team", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        sendSuccessMessage(message, "Not implemented yet");
    }
}
