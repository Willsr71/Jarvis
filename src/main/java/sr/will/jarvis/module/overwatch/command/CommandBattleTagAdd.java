package sr.will.jarvis.module.overwatch.command;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
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

        User user = message.getAuthor();
        if (getMentionedUser(message, args) != null) {
            user = getMentionedUser(message, args);
        }

        String battletag = module.getBattletag(user.getId());
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

        module.addBattletag(user.getId(), battletag);
        sendSuccessMessage(message, "Account " + user.getAsMention() + " linked to battletag " + battletag, false);
    }
}
