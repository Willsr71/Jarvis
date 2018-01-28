package sr.will.jarvis.modules.assistance.command;

import net.dv8tion.jda.core.entities.Message;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.assistance.ModuleAssistance;

public class CommandRemindme extends Command {
    private ModuleAssistance module;

    public CommandRemindme(ModuleAssistance module) {
        super("remindme", "remindme <delay> <text>", "Metions the sender after the specified delay time", module);
        this.module = module;
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
            module.addReminder(message.getAuthor().getIdLong(), message.getChannel().getIdLong(), duration, condenseArgs(args, 1));
        } catch (Exception e) {
            sendFailureMessage(message, "Invalid time");
            return;
        }

        sendSuccessMessage(message, "You will be reminded in " + DateUtils.formatDateDiff(duration));
    }
}
