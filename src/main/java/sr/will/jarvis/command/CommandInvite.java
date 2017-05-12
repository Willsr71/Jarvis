package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

public class CommandInvite extends Command {
    private Jarvis jarvis;

    public CommandInvite(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        message.getChannel().sendMessage(Jarvis.getJda().asBot().getInviteUrl(jarvis.moduleManager.getNeededPermissions())).queue();
    }

    @Override
    public String getUsage() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Displays an invite link for the bot";
    }

    @Override
    public boolean getModuleEnabled(long guildId) {
        return true;
    }
}
