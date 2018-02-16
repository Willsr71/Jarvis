package sr.will.jarvis.modules.elections.manager;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.modules.elections.Election;
import sr.will.jarvis.modules.elections.ElectionState;
import sr.will.jarvis.modules.elections.ModuleElections;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ElectionManager {
    private ModuleElections module;

    private ArrayList<Election> elections = new ArrayList<>();

    public ElectionManager(ModuleElections module) {
        this.module = module;
    }

    public void finishStartup() {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT * from elections;");
            while (result.next()) {
                elections.add(new Election(
                        module,
                        result.getLong("guild"),
                        result.getString("name"),
                        result.getInt("day_of_month"),
                        result.getLong("voting_period"),
                        ElectionState.valueOf(result.getString("election_state")),
                        result.getString("form_id"),
                        module.getRegistrantsFromIdString(result.getString("registrants"))
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Election> getElectionsByGuild(long guildId) {
        ArrayList<Election> guildElections = new ArrayList<>(elections);
        guildElections.removeIf(election -> election.getGuildId() == guildId);
        return guildElections;
    }
}
