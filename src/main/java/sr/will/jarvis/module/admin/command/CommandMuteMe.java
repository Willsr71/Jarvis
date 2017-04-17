package sr.will.jarvis.module.admin.command;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.admin.ModuleAdmin;

public class CommandMuteMe extends Command {
    private ModuleAdmin module;

    public CommandMuteMe(ModuleAdmin module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        User user = message.getAuthor();

        if (args.length == 0) {
            sendSuccessEmote(message);
            module.muteManager.mute(message.getGuild().getIdLong(), user.getIdLong(), message.getAuthor().getIdLong());
            return;
        }

        long duration = 0;

        try {
            duration = DateUtils.parseDateDiff(args[0], true);
        } catch (Exception e) {
            sendFailureMessage(message, "Invalid time");
            return;
        }

        sendSuccessMessage(message, user.getAsMention() + " has been muted for " + DateUtils.formatDateDiff(duration));
        module.muteManager.mute(message.getGuild().getIdLong(), user.getIdLong(), message.getAuthor().getIdLong(), duration);
    }
}
