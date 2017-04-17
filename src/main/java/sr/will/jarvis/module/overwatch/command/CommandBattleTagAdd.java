package sr.will.jarvis.module.overwatch.command;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.overwatch.ModuleOverwatch;
import sr.will.jarvis.rest.owapi.UserStats;

public class CommandBattleTagAdd extends Command {
    private ModuleOverwatch module;

    public CommandBattleTagAdd(ModuleOverwatch module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        String battletag = module.getBattletag(message.getAuthor().getId());
        if (battletag != null) {
            sendFailureMessage(message, "Account already linked to battletag " + battletag);
            return;
        }

        if (args.length == 0) {
            sendFailureMessage(message, "No battletag specified");
            return;
        }

        battletag = args[0].replace("#", "-");
        if (!module.isValidBattleTag(battletag)) {
            sendFailureMessage(message, "Invalid battletag");
            return;
        }

        UserStats userStats;
        try {
            userStats = module.getUserStats(battletag);
        } catch (UnirestException e) {
            e.printStackTrace();
            sendFailureMessage(message, "An error occurred");
            return;
        }

        if (userStats.error != null) {
            sendFailureMessage(message, userStats.msg);
            return;
        }

        module.addBattletag(message.getAuthor().getId(), battletag);
        sendSuccessMessage(message, "Account linked to battletag " + battletag, false);
    }
}
