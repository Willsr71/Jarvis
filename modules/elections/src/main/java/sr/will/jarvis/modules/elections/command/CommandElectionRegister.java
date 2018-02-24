package sr.will.jarvis.modules.elections.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.elections.ModuleElections;
import sr.will.jarvis.modules.elections.entity.Election;
import sr.will.jarvis.modules.elections.entity.ElectionState;

public class CommandElectionRegister extends Command {
    private ModuleElections module;

    public CommandElectionRegister(ModuleElections module) {
        super("electionregister", "electionregister <name>", "Register to take part in an election", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

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

        long userId = message.getAuthor().getIdLong();
        if (election.getRegistrantById(userId) == null) {
            election.addRegistrant(userId);
            sendSuccessMessage(message, "You are now registered in the " + election.name + " election", false);
        } else {
            election.removeRegistrant(userId);
            sendSuccessMessage(message, "You are no longer registered in the " + election.name + " election", false);
        }
    }
}
