package sr.will.jarvis.modules.elections.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.elections.ModuleElections;
import sr.will.jarvis.modules.elections.entity.Election;

public class CommandElectionRemove extends Command {
    private ModuleElections module;

    public CommandElectionRemove(ModuleElections module) {
        super("electionremove", "electionremove <name>", "Removes an election", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.ADMINISTRATOR);

        if (args.length != 1) {
            sendUsage(message);
            return;
        }

        String name = args[0];

        if (module.getElectionByName(message.getGuild().getIdLong(), name) == null) {
            sendFailureMessage(message, "Election does not exists");
            return;
        }

        Election election = module.getElectionByName(message.getGuild().getIdLong(), name);
        module.removeElection(election);
        sendSuccessMessage(message, "Removed election " + name);
    }
}
