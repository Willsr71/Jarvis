package sr.will.jarvis.modules.elections.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.elections.ModuleElections;
import sr.will.jarvis.modules.elections.entity.Election;

import java.awt.*;
import java.util.ArrayList;

public class CommandElection extends Command {
    private ModuleElections module;

    public CommandElection(ModuleElections module) {
        super("election", "election", "Election command", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.ADMINISTRATOR);

        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Election> elections = module.getElectionsByGuild(message.getGuild().getIdLong());

        if (elections.size() == 0) {
            stringBuilder.append("None");
        } else {

            for (Election election : elections) {
                stringBuilder.append(election.name).append(", ");
                stringBuilder.append("Day: ").append(election.dayOfMonth).append(", ");
                stringBuilder.append("Voting Period: ").append(module.formatVotingPeriod(election.votingPeriod));
                stringBuilder.append('\n');
            }
        }

        message.getChannel().sendMessage(new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("Elections")
                .setDescription(stringBuilder.toString())
                .build()).queue();
    }
}
