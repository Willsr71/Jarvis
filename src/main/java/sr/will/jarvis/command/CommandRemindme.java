package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

public class CommandRemindme extends Command {
    private Jarvis jarvis;

    public CommandRemindme(Jarvis jarvis) {
        super("remindme", "remindme <delay> <text>", "Metions the sender after the specified delay time", null);
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (args.length < 2) {
            sendFailureMessage(message, "Usage: remindme <delay> <text>");
            return;
        }

        long duration = 0;
        try {
            duration = DateUtils.parseDateDiff(args[0], true);
            jarvis.reminderManager.addReminder(message.getAuthor().getIdLong(), message.getChannel().getIdLong(), duration, condenseArgs(args, 1));
        } catch (Exception e) {
            sendFailureMessage(message, "Invalid time");
            return;
        }

        sendSuccessMessage(message, "You will be reminded in " + DateUtils.formatDateDiff(duration));
    }
}
