package sr.will.jarvis.modules.info.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.info.ModuleInfo;

public class CommandWhatIs extends Command {
    private ModuleInfo module;

    public CommandWhatIs(ModuleInfo module) {
        super("?", "? <id>", "Show what an ID is", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        if (args.length == 0) {
            sendUsage(message);
            return;
        }

        long id = Long.parseLong(args[0]);

        if (id == 0) {
            sendFailureMessage(message, "Invalid ID");
            return;
        }

        JDA jda = Jarvis.getJda();

        if (jda.getUserById(id) != null) {
            sendSuccessMessage(message, "User: " + jda.getUserById(id).getName() + "#" + jda.getUserById(id).getDiscriminator());
        } else if (jda.getGuildById(id) != null) {
            sendSuccessMessage(message, "Guild: " + jda.getGuildById(id).getName());
        } else if (jda.getCategoryById(id) != null) {
            sendSuccessMessage(message, "Category: " + jda.getCategoryById(id).getName() + " (Guild: " + jda.getCategoryById(id).getGuild().getName() + ")");
        } else if (jda.getEmoteById(id) != null) {
            sendSuccessMessage(message, "Emote: " + jda.getEmoteById(id).getName() + " (Guild: " + jda.getEmoteById(id).getGuild().getName() + ")");
        } else if (jda.getRoleById(id) != null) {
            sendSuccessMessage(message, "Role: " + jda.getRoleById(id).getName() + " (Guild: " + jda.getRoleById(id).getGuild().getName() + ")");
        } else if (jda.getPrivateChannelById(id) != null) {
            sendSuccessMessage(message, "Private Channel: " + jda.getPrivateChannelById(id).getName());
        } else if (jda.getTextChannelById(id) != null) {
            sendSuccessMessage(message, "Text Channel: " + jda.getTextChannelById(id).getName() + " (Guild: " + jda.getTextChannelById(id).getGuild().getName() + ")");
        } else if (jda.getVoiceChannelById(id) != null) {
            sendSuccessMessage(message, "Voice Channel: " + jda.getVoiceChannelById(id).getName() + " (Guild: " + jda.getVoiceChannelById(id).getGuild().getName() + ")");
        } else {
            sendFailureMessage(message, "Unknown ID");
        }
    }
}
