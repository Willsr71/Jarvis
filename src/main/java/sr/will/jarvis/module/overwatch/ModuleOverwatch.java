package sr.will.jarvis.module.overwatch;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.overwatch.command.CommandBattleTagAdd;
import sr.will.jarvis.module.overwatch.command.CommandBattleTagRemove;
import sr.will.jarvis.module.overwatch.command.CommandOWStats;
import sr.will.jarvis.rest.owapi.UserStats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuleOverwatch extends Module {
    public static String BATTLETAG_REGEX = "(?i).{3,12}[\\#\\-][0-9]{4,5}";
    private Jarvis jarvis;

    public ModuleOverwatch(Jarvis jarvis) {
        this.jarvis = jarvis;

        jarvis.commandManager.registerCommand("battletagadd", new CommandBattleTagAdd(this));
        jarvis.commandManager.registerCommand("battletagremove", new CommandBattleTagRemove(this));
        jarvis.commandManager.registerCommand("owstats", new CommandOWStats(this));
    }

    @Override
    public void finishStart() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getName() {
        return "Overwatch";
    }

    @Override
    public String getHelpText() {
        return "Overwatch related commands";
    }

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        return new ArrayList<>(Arrays.asList(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE
        ));
    }

    @Override
    public boolean isDefaultEnabled() {
        return false;
    }

    public String getBattletagGroup(String arg) {
        Matcher matcher = Pattern.compile(BATTLETAG_REGEX).matcher(arg);
        return matcher.group();
    }

    public boolean isValidBattleTag(String battletag) {
        Matcher matcher = Pattern.compile(BATTLETAG_REGEX).matcher(battletag);
        return matcher.find();
    }

    public UserStats getUserStats(String battletag) throws UnirestException {
        Gson gson = new Gson();
        String string = Unirest.get("https://owapi.net/api/v3/u/" + battletag.replace("#", "-") + "/stats").asString().getBody();
        return gson.fromJson(string, UserStats.class);
    }

    public void addBattletag(String userId, String battletag) {
        jarvis.database.execute("INSERT INTO overwatch_accounts (user, battletag) VALUES (?, ?);", userId, battletag);
    }

    public void removeBattletag(String userid) {
        jarvis.database.execute("DELETE FROM overwatch_accounts WHERE (user = ?);", userid);
    }

    public String getBattletag(String userid) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT battletag FROM overwatch_accounts WHERE (user = ?);", userid);
            if (result.first()) {
                return result.getString("battletag");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
