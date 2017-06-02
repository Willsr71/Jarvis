package sr.will.jarvis.module.levels.command;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.levels.ModuleLevels;
import sr.will.jarvis.rest.mee6.Levels;

import java.util.Date;

public class CommandImportMee6 extends Command {
    private ModuleLevels module;

    public CommandImportMee6(ModuleLevels module) {
        super("importmee6", "importmee6", "Imports levels from mee6", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.ADMINISTRATOR);

        long startTime = new Date().getTime();

        Levels levels = null;
        try {
            levels = getMee6Levels(message.getGuild().getIdLong());
        } catch (JsonSyntaxException e) {
            sendFailureMessage(message, "Mee6 does not exist on this server");
        } catch (UnirestException e) {
            e.printStackTrace();
            sendFailureMessage(message, "An error occured");
        }
        if (levels == null) {
            return;
        }

        for (Levels.Player player : levels.players) {
            System.out.println(player.name + " = " + player.total_xp);

            if (!module.userExists(message.getGuild().getIdLong(), player.id)) {
                module.addUser(message.getGuild().getIdLong(), player.id, player.total_xp);
            } else {
                module.setUserXp(message.getGuild().getIdLong(), player.id, player.total_xp);
            }
        }

        sendSuccessMessage(message, "Imported " + levels.players.size() + " players from Mee6", false);
    }

    public Levels getMee6Levels(long guildId) throws UnirestException, JsonSyntaxException {
        Gson gson = new Gson();
        String string = Unirest.get("https://mee6.xyz/levels/" + guildId + "?json=1").asString().getBody();
        Levels levels = gson.fromJson(string, Levels.class);
        return levels;
    }
}
