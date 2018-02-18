package sr.will.jarvis.modules.elections.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.elections.ModuleElections;
import sr.will.jarvis.modules.elections.entity.Election;
import sr.will.jarvis.modules.elections.entity.ElectionState;

public class CommandElectionStart extends Command {
    private ModuleElections module;

    public CommandElectionStart(ModuleElections module) {
        super("electionstart", "electionstart <name>", "Start an election", module);
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
        Election election = module.getElectionByName(message.getGuild().getIdLong(), name);

        if (election == null) {
            sendFailureMessage(message, "Election does not exists");
            return;
        }

        if (election.electionState == ElectionState.VOTING) {
            sendFailureMessage(message, "Election is already started");
            return;
        }

        election.startElection();
        sendSuccessEmote(message);
    }
}
