package sr.will.jarvis.modules.elections.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.elections.ModuleElections;
import sr.will.jarvis.modules.elections.entity.Election;

public class CommandElectionAdd extends Command {
    private ModuleElections module;

    public CommandElectionAdd(ModuleElections module) {
        super("electionadd", "electionadd <name> <day of month> <voting period> <announcement channel>", "Adds an election", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.ADMINISTRATOR);

        if (args.length != 4 || message.getMentionedChannels().size() == 0) {
            sendUsage(message);
            return;
        }

        String name = args[0];
        int dayOfMonth = Integer.parseInt(args[1]);
        long votingPeriod = 0;
        try {
            votingPeriod = DateUtils.parseDateDiffAbsolute(args[2], true).getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
            sendFailureMessage(message, "Invalid voting period");
            return;
        }
        MessageChannel channel = message.getMentionedChannels().get(0);

        if (module.getElectionByName(message.getGuild().getIdLong(), name) != null) {
            sendFailureMessage(message, "Election already exists");
            return;
        }

        module.addElection(new Election(
                module,
                message.getGuild().getIdLong(),
                name,
                channel.getIdLong(),
                dayOfMonth,
                votingPeriod
        ));
        sendSuccessMessage(message, "Added election **" + name + "** set for day **" + dayOfMonth + "** with a voting period of **" + module.formatVotingPeriod(votingPeriod) + "** (" + votingPeriod + ")", false);
    }
}
