package sr.will.jarvis.modules.elections;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.elections.command.CommandElection;
import sr.will.jarvis.modules.elections.manager.ElectionManager;
import sr.will.jarvis.modules.elections.rest.formManager.FormCreate;
import sr.will.jarvis.modules.elections.rest.formManager.FormGet;

import java.util.ArrayList;

public class ModuleElections extends Module {
    public ElectionManager electionManager;

    public static final String formManagerUrl = "https://script.google.com/macros/s/AKfycbz7zqjzcXGGx9Q7UQTKpZnfLN7iql5V4_cs2VLsL1L_le81Zuk/exec";
    private Gson gson = new Gson();

    public void initialize() {
        setNeededPermissions(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE,
                Permission.MESSAGE_ADD_REACTION,
                Permission.MESSAGE_MANAGE,
                Permission.MESSAGE_MENTION_EVERYONE
        );
        setDefaultEnabled(false);

        electionManager = new ElectionManager(this);

        registerCommand("election", new CommandElection(this));
    }

    public void finishStart() {
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS elections(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "name varchar(255) NOT NULL," +
                "day_of_month bigint(20) NOT NULL," +
                "voting_period bigint(20) NOT NULL," +
                "election_state varchar(16) NOT NULL," +
                "form_id char(44) NOT NULL," +
                "registrants text NOT NULL," +
                "PRIMARY KEY (id));");

        electionManager.finishStartup();
    }

    public void stop() {

    }

    public void reload() {

    }

    public FormCreate createPoll(String name, String registrants) {
        try {
            String string = Unirest.get(formManagerUrl + "?action=create&name=" + name + "&choices=" + registrants).asString().getBody();
            return gson.fromJson(string, FormCreate.class);
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

    public FormGet getResponses(String formId) {
        try {
            String string = Unirest.get(formManagerUrl + "?formId=" + formId).asString().getBody();
            return gson.fromJson(string, FormGet.class);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRegistrantsAsString(ArrayList<Registrant> registrants) {
        StringBuilder string = new StringBuilder();
        for (Registrant registrant : registrants) {
            string.append(registrant.getUser().getDiscriminator()).append(",");
        }
        string.setLength(string.length() - 1);

        return string.toString();
    }

    public String getRegistrantsAsIdString(ArrayList<Registrant> registrants) {
        StringBuilder string = new StringBuilder();
        for (Registrant registrant : registrants) {
            string.append(registrant.getUser().getId()).append(",");
        }
        string.setLength(string.length() - 1);

        return string.toString();
    }

    public ArrayList<Registrant> getRegistrantsFromIdString(String string) {
        ArrayList<Registrant> registrants = new ArrayList<>();
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
}
