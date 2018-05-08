package sr.will.jarvis.modules.elections.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.elections.ModuleElections;
import sr.will.jarvis.modules.elections.entity.Election;
import sr.will.jarvis.modules.elections.entity.Registrant;

import java.awt.*;

public class CommandElectionViewRegistrants extends Command {
    private ModuleElections module;

    public CommandElectionViewRegistrants(ModuleElections module) {
        super("electionviewregistrants", "electionviewregistrants <name>", "View the registered members of an election", module);
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

        StringBuilder registrants = new StringBuilder();
        for (Registrant registrant : election.getRegistrants()) {
            registrants.append(registrant.getUser().getName()).append("\n");
        }

        if (election.getRegistrants().size() == 0) {
            registrants.append("None");
        }

        message.getChannel().sendMessage(
                new EmbedBuilder()
                        .setTitle("Registrants for " + election.getElectionName(), null)
                        .setColor(Color.GREEN)
                        .setDescription(registrants)
                        .build()).queue();
    }
}
