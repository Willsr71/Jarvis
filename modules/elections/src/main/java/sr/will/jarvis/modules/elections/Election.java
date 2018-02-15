package sr.will.jarvis.modules.elections;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.modules.elections.rest.formManager.FormCreate;
import sr.will.jarvis.modules.elections.rest.formManager.FormGet;

import java.util.ArrayList;
import java.util.Comparator;

public class Election {
    private ModuleElections module;
    public long guildId;
    public String name;
    public int dayOfMonth;
    public long votingPeriod;
    public ElectionState electionState;
    public String formId;
    public ArrayList<Registrant> registrants;

    public Election(ModuleElections module, long guildId, String name, int dayOfMonth, long votingPeriod, ElectionState electionState, String formId, ArrayList<Registrant> registrants) {
        this.module = module;
        this.guildId = guildId;
        this.name = name;
        this.dayOfMonth = dayOfMonth;
        this.votingPeriod = votingPeriod;
        this.electionState = electionState;
        this.formId = formId;
        this.registrants = registrants;
    }

    public void createPoll() throws UnirestException {
        Gson gson = new Gson();
        String string = Unirest.get(ModuleElections.formManagerUrl + "?action=create&name=" + name + "&choices=" + module.getRegistrantsAsString(registrants)).asString().getBody();
        FormCreate formCreate = gson.fromJson(string, FormCreate.class);
        this.formId = formCreate.form_id;
    }

    public FormGet getResponses() {
        try {
            Gson gson = new Gson();
            String string = Unirest.get(ModuleElections.formManagerUrl + "?formId=" + formId).asString().getBody();
            return gson.fromJson(string, FormGet.class);
        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void countVotes() {
        FormGet responses = getResponses();

        for (FormGet.Response response : responses.userResponses) {
            if (!userExistsOnGuild(response.username)) {
                System.out.println("User " + response.username + " thrown out, does no exist on guild");
                continue;
            }

            response.votes.forEach((discriminator) -> getRegistrantByDiscriminator(discriminator).addVote());
        }
    }

    public ArrayList<Registrant> getLeaderboard() {
        countVotes();

        registrants.sort(Comparator.comparingInt(a -> a.votes));
        return registrants;
    }

    public Registrant getRegistrantByDiscriminator(String discriminator) {
        for (Registrant registrant : registrants) {
            if (registrant.getUser().getDiscriminator().equals(discriminator)) {
                return registrant;
            }
        }

        return null;
    }

    public boolean userExistsOnGuild(String username) {
        Guild guild = Jarvis.getJda().getGuildById(guildId);
        for (Member member : guild.getMembers()) {
            if (member.getUser().getDiscriminator().equals(username)) {
                return true;
            }
        }

        return false;
    }

    public enum ElectionState {
        SCHEDULED,
        REGISTRATION,
        VOTING,
        FINISHED;
    }
}
