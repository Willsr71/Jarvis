package sr.will.jarvis.modules.elections.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.elections.Election;
import sr.will.jarvis.modules.elections.ModuleElections;

import java.awt.*;

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
        for (Election election : module.electionManager.getElectionsByGuild(message.getGuild().getIdLong())) {
            stringBuilder.append(election.getName()).append('\n');
        }

        message.getChannel().sendMessage(new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("Elections")
                .setDescription(stringBuilder.toString())
                .build()).queue();
    }
}
