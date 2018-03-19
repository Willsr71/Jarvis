package sr.will.jarvis.modules.overwatch;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.overwatch.command.CommandBattleTagAdd;
import sr.will.jarvis.modules.overwatch.command.CommandBattleTagRemove;
import sr.will.jarvis.modules.overwatch.command.CommandOWHeroes;
import sr.will.jarvis.modules.overwatch.command.CommandOWStats;
import sr.will.jarvis.modules.overwatch.rest.ovrstat.UserInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuleOverwatch extends Module {
    private static String BATTLETAG_REGEX = "(?i).{3,12}[\\#\\-][0-9]{4,5}";
    private static ArrayList<String> tiers = new ArrayList<>(Arrays.asList("bronze", "silver", "gold", "platinum", "diamond", "master", "grandmaster"));

    public void initialize() {
        setNeededPermissions(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE
        );
        setDefaultEnabled(false);

        registerCommand("battletagadd", new CommandBattleTagAdd(this));
        registerCommand("battletagremove", new CommandBattleTagRemove(this));
        registerCommand("owheroes", new CommandOWHeroes(this));
        registerCommand("owstats", new CommandOWStats(this));
    }

    public void finishStart() {
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS overwatch_accounts(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "user bigint(20) NOT NULL," +
                "battletag char(20) NOT NULL," +
                "PRIMARY KEY (id));");
    }

    public void stop() {

    }

    public void reload() {

    }

    public boolean isValidBattleTag(String battletag) {
        Matcher matcher = Pattern.compile(BATTLETAG_REGEX).matcher(battletag);
        return matcher.find();
    }

    public UserInfo getUserInfo(String battletag) throws UnirestException {
        Gson gson = new Gson();
        String string = Unirest.get("https://ovrstat.com/stats/pc/us/" + battletag).asString().getBody();
        UserInfo userInfo = gson.fromJson(string, UserInfo.class);

        // Additional information
        userInfo.battletag = userInfo.name + "-" + battletag.split("-")[1];
        userInfo.playOverwatchUrl = "https://playoverwatch.com/en-us/career/pc/us/" + userInfo.battletag;

        // Add hero name to hero object
        userInfo.quickPlayStats.topHeroes.forEach((name, hero) -> hero.name = name);
        userInfo.competitiveStats.topHeroes.forEach((name, hero) -> hero.name = name);

        return userInfo;
    }

    public void addBattletag(long userId, String battletag) {
        Jarvis.getDatabase().execute("INSERT INTO overwatch_accounts (user, battletag) VALUES (?, ?);", userId, battletag);
    }

    public void removeBattletag(long userid) {
        Jarvis.getDatabase().execute("DELETE FROM overwatch_accounts WHERE (user = ?);", userid);
    }

    public String getBattletag(long userId) {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT battletag FROM overwatch_accounts WHERE (user = ?);", userId);
            if (result.first()) {
                return result.getString("battletag");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<UserInfo.Stats.TopHero> sortHeroesByTime(ArrayList<UserInfo.Stats.TopHero> heroes) {
        heroes.sort(Comparator.comparingInt(o -> o.timePlayedInSeconds));
        Collections.reverse(heroes);
        return heroes;
    }

    public String getTopHeroesAsString(HashMap<String, UserInfo.Stats.TopHero> heroes, int size) {
        ArrayList<UserInfo.Stats.TopHero> topHeroes = sortHeroesByTime(new ArrayList<>(heroes.values()));
        StringBuilder topHeroesBuilder = new StringBuilder();
        int x = 0;
        for (UserInfo.Stats.TopHero hero : topHeroes) {
            if (hero.timePlayedInSeconds == 0) {
                continue;
            }

            topHeroesBuilder.append(Command.capitalizeProperly(hero.name)).append(" (").append(hero.timePlayed).append(")");
            x += 1;
            if (x >= size) {
                break;
            }

            topHeroesBuilder.append("\n");
        }

        return topHeroesBuilder.toString();
    }

    public String getTopHeroesAsString(HashMap<String, UserInfo.Stats.TopHero> heroes) {
        return getTopHeroesAsString(heroes, heroes.size());
    }

    public UserInfo getUserInfo(Message message, String... args) {
        String battletag;

        if (args.length != 0) {
            if (isValidBattleTag(args[0])) {
                battletag = args[0].replace("#", "-");
            } else if (Command.getMentionedUser(message, args) != null) {
                battletag = getBattletag(Command.getMentionedUser(message, args).getIdLong());
            } else {
                Command.sendFailureMessage(message, "Invalid battletag");
                return null;
            }
        } else {
            battletag = getBattletag(message.getAuthor().getIdLong());
        }

        if (battletag == null) {
            Command.sendFailureMessage(message, "No battletag specified or linked");
            return null;
        }

        UserInfo userInfo;
        try {
            userInfo = getUserInfo(battletag);
        } catch (UnirestException e) {
            e.printStackTrace();
            Command.sendFailureMessage(message, "An error occurred");
            return null;
        }

        if (userInfo.status != 0) {
            Command.sendFailureMessage(message, userInfo.message);
            return null;
        }

        return userInfo;
    }
}
