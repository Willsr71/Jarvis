package sr.will.jarvis.modules.elections;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.elections.command.CommandElection;
import sr.will.jarvis.modules.elections.command.CommandElectionAdd;
import sr.will.jarvis.modules.elections.command.CommandElectionRemove;
import sr.will.jarvis.modules.elections.command.CommandElectionStart;
import sr.will.jarvis.modules.elections.entity.Election;
import sr.will.jarvis.modules.elections.entity.ElectionState;
import sr.will.jarvis.modules.elections.entity.Registrant;
import sr.will.jarvis.modules.elections.rest.formManager.FormCreate;
import sr.will.jarvis.modules.elections.rest.formManager.FormGet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ModuleElections extends Module {
    public static final String formManagerUrl = "https://script.google.com/macros/s/AKfycbz7zqjzcXGGx9Q7UQTKpZnfLN7iql5V4_cs2VLsL1L_le81Zuk/exec";
    private Gson gson = new Gson();

    private ArrayList<Election> elections = new ArrayList<>();

    public void initialize() {
        setNeededPermissions(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_MANAGE,
                Permission.MESSAGE_MENTION_EVERYONE
        );
        setDefaultEnabled(false);

        registerCommand("election", new CommandElection(this));
        registerCommand("electionadd", new CommandElectionAdd(this));
        registerCommand("electionremove", new CommandElectionRemove(this));
        registerCommand("electionstart", new CommandElectionStart(this));
    }

    public void finishStart() {
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS elections(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "name varchar(255) NOT NULL," +
                "channel bigint(20) NOT NULL," +
                "day_of_month bigint(20) NOT NULL," +
                "voting_period bigint(20) NOT NULL," +
                "election_state varchar(16) NOT NULL," +
                "form_id char(44)," +
                "registrants text NOT NULL," +
                "PRIMARY KEY (id));");

        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT guild, name, channel, day_of_month, voting_period, election_state, form_id, registrants from elections;");
            while (result.next()) {
                elections.add(new Election(
                        this,
                        result.getLong("guild"),
                        result.getString("name"),
                        result.getLong("channel"),
                        result.getInt("day_of_month"),
                        result.getLong("voting_period"),
                        ElectionState.valueOf(result.getString("election_state")),
                        result.getString("form_id"),
                        getRegistrantsFromIdString(result.getString("registrants"))
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stop() {

    }

    public void reload() {

    }

    public void addElection(Election election) {
        elections.add(election);
        Jarvis.getDatabase().execute("INSERT INTO elections (guild, name, channel, day_of_month, voting_period, election_state, form_id, registrants) VALUES (?, ?, ?, ?, ?, ?, ?, ?);",
                election.guildId, election.name, election.channelId, election.dayOfMonth, election.votingPeriod, election.electionState.toString(), election.formId, getRegistrantsAsIdString(election.getRegistrants()));
    }

    public void removeElection(Election election) {
        elections.remove(election);
        Jarvis.getDatabase().execute("DELETE FROM elections WHERE (guild = ? AND name = ?);", election.guildId, election.name);
    }

    public void updateElectionState(long guildId, String name, ElectionState electionState) {
        Jarvis.getDatabase().execute("UPDATE elections SET election_state = ? WHERE (guild = ? AND name = ?);", electionState, guildId, name);
    }

    public void updateFormId(long guildId, String name, String formId) {
        Jarvis.getDatabase().execute("UPDATE elections SET form_id = ? WHERE (guild = ? AND name = ?);", formId, guildId, name);
    }

    public void updateRegistrants(long guildId, String name, ArrayList<Registrant> registrants) {
        Jarvis.getDatabase().execute("UPDATE elections SET registrants = ? WHERE (guild = ? AND name = ?);", getRegistrantsAsIdString(registrants), guildId, name);
    }

    public ArrayList<Election> getElectionsByGuild(long guildId) {
        ArrayList<Election> guildElections = new ArrayList<>(elections);
        guildElections.removeIf(election -> election.guildId != guildId);
        return guildElections;
    }

    public Election getElectionByName(long guildId, String name) {
        for (Election election : getElectionsByGuild(guildId)) {
            if (election.name.equals(name)) {
                return election;
            }
        }

        return null;
    }

    public FormCreate createPoll(String name, ArrayList<Registrant> registrants) {
        String requestString = formManagerUrl + "?action=create&name=" + name + "&choices=" + getRegistrantsAsString(registrants);
        Jarvis.debug(requestString);
        try {
            String string = Unirest.get(requestString).asString().getBody();
            return gson.fromJson(string, FormCreate.class);
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

    public FormGet getResponses(String formId) {
        String requestString = formManagerUrl + "?formId=" + formId;
        Jarvis.debug(requestString);
        try {
            String string = Unirest.get(requestString).asString().getBody();
            return gson.fromJson(string, FormGet.class);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRegistrantsAsString(ArrayList<Registrant> registrants) {
        if (registrants.size() == 0) {
            return "";
        }

        StringBuilder string = new StringBuilder();
        for (Registrant registrant : registrants) {
            string.append(registrant.getUser().getDiscriminator()).append(",");
        }
        string.setLength(string.length() - 1);

        return string.toString();
    }

    public String getRegistrantsAsIdString(ArrayList<Registrant> registrants) {
        if (registrants.size() == 0) {
            return "";
        }

        StringBuilder string = new StringBuilder();
        for (Registrant registrant : registrants) {
            string.append(registrant.getUser().getId()).append(",");
        }
        string.setLength(string.length() - 1);

        return string.toString();
    }

    public ArrayList<Registrant> getRegistrantsFromIdString(String string) {
        ArrayList<Registrant> registrants = new ArrayList<>();
        if (string.length() == 0) {
            return registrants;
        }

        for (String userId : string.split(",")) {
            registrants.add(new Registrant(Long.valueOf(userId)));
        }

        return registrants;
    }

    public boolean userExistsOnGuild(long guildId, String discriminator) {
        Guild guild = Jarvis.getJda().getGuildById(guildId);
        for (Member member : guild.getMembers()) {
            if (member.getUser().getDiscriminator().equals(discriminator)) {
                return true;
            }
        }

        return false;
    }

    public long getTomorrow() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime midnight = ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0, 0, 0, now.getZone());
        return midnight.plusDays(1).toInstant().toEpochMilli();
    }

    public String formatVotingPeriod(long votingPeriod) {
        GregorianCalendar zero = new GregorianCalendar();
        zero.setTimeInMillis(0);
        GregorianCalendar voting = new GregorianCalendar();
        voting.setTimeInMillis(votingPeriod);

        return DateUtils.formatDateDiff(zero, voting);
    }
}
