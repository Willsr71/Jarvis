package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.util.CommandUtils;

public class CommandMuteMe extends Command {
    private Jarvis jarvis;

    public CommandMuteMe(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        User user = message.getAuthor();

        if (args.length == 0) {
            CommandUtils.sendSuccessEmote(message);
            jarvis.muteManager.mute(message.getGuild().getId(), user.getId(), message.getAuthor().getId());
            return;
        }

        long duration = 0;

        try {
            duration = DateUtils.parseDateDiff(args[0], true);
        } catch (Exception e) {
            CommandUtils.sendFailureMessage(message, "Invalid time");
            return;
        }

        CommandUtils.sendSuccessMessage(message, user.getAsMention() + " has been muted for " + DateUtils.formatDateDiff(duration));
        jarvis.muteManager.mute(message.getGuild().getId(), user.getId(), message.getAuthor().getId(), duration);
    }
}
