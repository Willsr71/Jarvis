package sr.will.jarvis.modules.elections.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.elections.ModuleElections;
import sr.will.jarvis.modules.elections.entity.Election;
import sr.will.jarvis.modules.elections.entity.ElectionState;

public class CommandElectionVoteAll extends Command {
    private ModuleElections module;

    public CommandElectionVoteAll(ModuleElections module) {
        super("electionvoteall", "electionvoteall <name>", "Send a link for voting to everyone on the guild", module);
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
            sendFailureMessage(message, "Election does not exist");
            return;
        }

        if (election.electionState == ElectionState.SCHEDULED) {
            sendFailureMessage(message, "Election has not started");
            return;
        }

        election.distributePoll();
        sendSuccessEmote(message);
    }
}
