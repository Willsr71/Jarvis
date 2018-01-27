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
import sr.will.jarvis.rest.owapi.UserBlob;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModuleOverwatch extends Module {
    private static String BATTLETAG_REGEX = "(?i).{3,12}[\\#\\-][0-9]{4,5}";
    private static ArrayList<String> tiers = new ArrayList<>(Arrays.asList("bronze", "silver", "gold", "platinum", "diamond", "master", "grandmaster"));

    public void initialize() {
        setDescription("Overwatch", "Overwatch related commands");
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

    public UserBlob getUserBlob(String battletag) throws UnirestException {
        Gson gson = new Gson();
        String string = Unirest.get("https://owapi.net/api/v3/u/" + battletag.replace("#", "-") + "/blob").asString().getBody();
        UserBlob userBlob = gson.fromJson(string, UserBlob.class);
        userBlob.battletag = battletag;
        return userBlob;
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

    public String getTierImage(String tier) {
        for (int x = 0; x < tiers.size(); x += 1) {
            if (tiers.get(x).equals(tier)) {
                return "https://blzgdapipro-a.akamaihd.net/game/rank-icons/season-2/rank-" + (x + 1) + ".png";
            }
        }

        return "https://blzgdapipro-a.akamaihd.net/game/rank-icons/season-2/rank-1.png";
    }

    public HashMap<String, Double> sortHeroesByTime(HashMap<String, Double> heroes) {
        return heroes.entrySet()
                .stream()
                .sorted(HashMap.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public String getTopHeroesAsString(HashMap<String, Double> heroes, int size) {
        HashMap<String, Double> topHeroes = sortHeroesByTime(heroes);
        StringBuilder topHeroesBuilder = new StringBuilder();
        int x = 0;
        for (String hero : topHeroes.keySet()) {
            double heroTime = topHeroes.get(hero);

            if (heroTime == 0) {
                continue;
            }

            topHeroesBuilder.append(Command.capitalizeProperly(hero)).append(" (");
            if (Math.round(heroTime) == heroTime) {
                topHeroesBuilder.append(Math.round(heroTime)).append(" hrs)");
            } else {
                topHeroesBuilder.append(Math.round(heroTime * 60)).append(" mins)");
            }

            x += 1;
            if (x >= size) {
                break;
            }

            topHeroesBuilder.append("\n");
        }

        return topHeroesBuilder.toString();
    }

    public String getTopHeroesAsString(HashMap<String, Double> heroes) {
        return getTopHeroesAsString(heroes, heroes.size());
    }

    public UserBlob getUserBlob(Message message, String... args) {
        String battletag = null;

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

        UserBlob userBlob;
        try {
            userBlob = getUserBlob(battletag);
        } catch (UnirestException e) {
            e.printStackTrace();
            Command.sendFailureMessage(message, "An error occurred");
            return null;
        }

        if (userBlob.error != null) {
            Command.sendFailureMessage(message, Command.capitalizeProperly(userBlob.msg));
            return null;
        }

        return userBlob;
    }
}
