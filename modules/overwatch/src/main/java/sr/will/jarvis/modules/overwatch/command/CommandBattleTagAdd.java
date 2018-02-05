package sr.will.jarvis.modules.overwatch.command;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.overwatch.ModuleOverwatch;
import sr.will.jarvis.modules.overwatch.rest.owapi.UserBlob;

public class CommandBattleTagAdd extends Command {
    private ModuleOverwatch module;

    public CommandBattleTagAdd(ModuleOverwatch module) {
        super("battletagadd", "battletagadd <battletag> [user mention|user id]", "Adds the battletag to the specified discord account to allow for mentioning", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        User user = message.getAuthor();
        if (getMentionedUser(message, args) != null) {
            user = getMentionedUser(message, args);
        }

        String battletag = module.getBattletag(user.getIdLong());
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

        UserBlob userBlob;
        try {
            userBlob = module.getUserBlob(battletag);
        } catch (UnirestException e) {
            e.printStackTrace();
            sendFailureMessage(message, "An error occurred");
            return;
        }

        if (userBlob.error != null) {
            sendFailureMessage(message, capitalizeProperly(userBlob.msg));
            return;
        }

        module.addBattletag(user.getIdLong(), battletag);
        sendSuccessMessage(message, "Account " + user.getAsMention() + " linked to battletag " + battletag, false);
    }
}
