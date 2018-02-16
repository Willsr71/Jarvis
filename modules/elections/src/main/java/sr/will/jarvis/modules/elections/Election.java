package sr.will.jarvis.modules.elections;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.modules.elections.rest.formManager.FormGet;

import java.util.ArrayList;
import java.util.Comparator;

public class Election {
    private ModuleElections module;
    private long guildId;
    private String name;
    private int dayOfMonth;
    private long votingPeriod;
    private ElectionState electionState;
    private String formId;
    private ArrayList<Registrant> registrants;

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

    public long getGuildId() {
        return guildId;
    }

    public String getName() {
        return name;
    }

    public void countVotes() {
        FormGet responses = module.getResponses(formId);

        for (FormGet.Response response : responses.userResponses) {
            if (!module.userExistsOnGuild(guildId, response.discriminator)) {
                System.out.println("User " + response.discriminator + " thrown out, does no exist on guild");
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

    public Registrant getRegistrantById(long userId) {
        for (Registrant registrant : registrants) {
            if (registrant.getUser().getIdLong() == userId) {
                return registrant;
            }
        }

        return null;
    }

    public void updateRegistrantsList() {
        Jarvis.getDatabase().execute("UPDATE elections SET registrants = ? WHERE (guild = ? AND name = ?);", module.getRegistrantsAsIdString(registrants), guildId, name);
    }

    public void addRegistrant(long userId) {
        registrants.add(new Registrant(userId));
        updateRegistrantsList();
    }

    public void removeRegistrant(long userId) {
        registrants.remove(getRegistrantById(userId));
        updateRegistrantsList();
    }

    public ArrayList<Registrant> getRegistrants() {
        return registrants;
    }
}
