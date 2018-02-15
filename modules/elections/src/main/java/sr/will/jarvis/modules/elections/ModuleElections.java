package sr.will.jarvis.modules.elections;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.elections.command.CommandElection;

import java.util.ArrayList;

public class ModuleElections extends Module {
    public static final String formManagerUrl = "https://script.google.com/macros/s/AKfycbz7zqjzcXGGx9Q7UQTKpZnfLN7iql5V4_cs2VLsL1L_le81Zuk/exec";

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
    }

    public void stop() {

    }

    public void reload() {

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
}
