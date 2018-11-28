package sr.will.jarvis.modules.elections;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.elections.command.*;
import sr.will.jarvis.modules.elections.entity.Election;
import sr.will.jarvis.modules.elections.entity.ElectionState;
import sr.will.jarvis.modules.elections.entity.Registrant;
import sr.will.jarvis.modules.elections.rest.formManager.FormClose;
import sr.will.jarvis.modules.elections.rest.formManager.FormCreate;
import sr.will.jarvis.modules.elections.rest.formManager.FormGet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Random;

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
        registerCommand("electionregister", new CommandElectionRegister(this));
        registerCommand("electionremove", new CommandElectionRemove(this));
        registerCommand("electionstart", new CommandElectionStart(this));
        registerCommand("electionstop", new CommandElectionStop(this));
        registerCommand("electionviewregistrants", new CommandElectionViewRegistrants(this));
        registerCommand("electionvote", new CommandElectionVote(this));
        registerCommand("electionvoteall", new CommandElectionVoteAll(this));
    }

    public void finishStart() {
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS elections(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "name varchar(255) NOT NULL," +
                "channel bigint(20) NOT NULL," +
                "role bigint(20) NOT NULL," +
                "winner_count int NOT NULL," +
                "day_of_month bigint(20) NOT NULL," +
                "voting_period bigint(20) NOT NULL," +
                "election_state varchar(16) NOT NULL," +
                "form_id char(44)," +
                "form_prefill varchar(1024)," +
                "registrants text NOT NULL," +
                "PRIMARY KEY (id));");

        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT guild, name, channel, role, winner_count, day_of_month, voting_period, election_state, form_id, form_prefill, registrants from elections;");
            while (result.next()) {
                // Ignore guilds that jarvis is not in
                if (Jarvis.getJda().getGuildById(result.getLong("guild")) == null) {
                    continue;
                }

                elections.add(new Election(
                        this,
                        result.getLong("guild"),
                        result.getString("name"),
                        result.getLong("channel"),
                        result.getLong("role"),
                        result.getInt("winner_count"),
                        result.getInt("day_of_month"),
                        result.getLong("voting_period"),
                        ElectionState.valueOf(result.getString("election_state")),
                        result.getString("form_id"),
                        result.getString("form_prefill"),
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
        Jarvis.getDatabase().execute("INSERT INTO elections (guild, name, channel, role, winner_count, day_of_month, voting_period, election_state, form_id, registrants) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);",
                election.guildId, election.name, election.channelId, election.roleId, election.winnerCount, election.dayOfMonth, election.votingPeriod, election.electionState.toString(), election.formId, getRegistrantsAsIdString(election.getRegistrants()));
    }

    public void removeElection(Election election) {
        elections.remove(election);
        Jarvis.getDatabase().execute("DELETE FROM elections WHERE (guild = ? AND name = ?);", election.guildId, election.name);
    }

    public void updateElectionState(long guildId, String name, ElectionState electionState) {
        Jarvis.getDatabase().execute("UPDATE elections SET election_state = ? WHERE (guild = ? AND name = ?);", electionState.toString(), guildId, name);
    }

    public void updateFormInfo(long guildId, String name, String formId, String formPrefill) {
        Jarvis.getDatabase().execute("UPDATE elections SET form_id = ?, form_prefill = ? WHERE (guild = ? AND name = ?);", formId, formPrefill, guildId, name);
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
        String requestString = formManagerUrl + "?action=create&name=" + URLEncode(name) + getRegistrantsAsChoiceString(registrants);

        Jarvis.getLogger().debug(requestString);
        try {
            String string = Unirest.get(requestString).asString().getBody();
            return gson.fromJson(string, FormCreate.class);
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

    public FormClose closePoll(String formId) {
        String requestString = formManagerUrl + "?action=close&formId=" + formId;
        Jarvis.getLogger().debug(requestString);
        try {
            String string = Unirest.get(requestString).asString().getBody();
            return gson.fromJson(string, FormClose.class);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FormGet getResponses(String formId) {
        String requestString = formManagerUrl + "?formId=" + formId;
        Jarvis.getLogger().debug(requestString);
        try {
            String string = Unirest.get(requestString).asString().getBody();
            return gson.fromJson(string, FormGet.class);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String URLEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    public String getAuthToken(long guildId, long userId, String electionName) {
        // Very secure token generation we got here
        Random random = new Random(guildId + userId + electionName.chars().sum());
        return Long.toHexString(random.nextLong());
    }

    public String getDiscriminator(User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public String getRegistrantsAsChoiceString(ArrayList<Registrant> registrants) {
        if (registrants.size() == 0) {
            return "";
        }

        StringBuilder string = new StringBuilder();
        for (Registrant registrant : registrants) {
            string.append("&choice=").append(URLEncode(getDiscriminator(registrant.getUser())));
        }

        return string.toString();
    }

    public String getRegistrantsAsIdString(ArrayList<Registrant> registrants) {
        if (registrants.size() == 0) {
            return "";
        }

        StringBuilder string = new StringBuilder();
        for (Registrant registrant : registrants) {
            string.append(registrant.userId).append(",");
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
