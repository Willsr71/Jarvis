package sr.will.jarvis.modules.elections.entity;

import net.dv8tion.jda.core.EmbedBuilder;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.modules.elections.ModuleElections;
import sr.will.jarvis.modules.elections.rest.formManager.FormCreate;
import sr.will.jarvis.modules.elections.rest.formManager.FormGet;
import sr.will.jarvis.thread.JarvisThread;

import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class Election {
    private ModuleElections module;
    public long guildId;
    public String name;
    public long channelId;
    public int dayOfMonth;
    public long votingPeriod;
    public ElectionState electionState;
    public String formId;
    private ArrayList<Registrant> registrants;

    public Election(ModuleElections module, long guildId, String name, long channelId, int dayOfMonth, long votingPeriod, ElectionState electionState, String formId, ArrayList<Registrant> registrants) {
        this.module = module;
        this.guildId = guildId;
        this.name = name;
        this.channelId = channelId;
        this.dayOfMonth = dayOfMonth;
        this.votingPeriod = votingPeriod;
        this.electionState = electionState;
        this.formId = formId;
        this.registrants = registrants;

        new JarvisThread(module, this::checkShouldStart).executeAt(module.getTomorrow()).repeat(true, 24 * 60 * 60 * 1000).name("Election-" + name).start();
        checkShouldStart();
    }

    public Election(ModuleElections module, long guildId, String name, long channelId, int dayOfMonth, long votingPeriod) {
        this(module, guildId, name, channelId, dayOfMonth, votingPeriod, ElectionState.SCHEDULED, null, new ArrayList<>());
    }

    public void checkShouldStart() {
        ZonedDateTime dateTime = ZonedDateTime.now();

        if (dateTime.getDayOfMonth() == dayOfMonth && electionState == ElectionState.SCHEDULED) {
            startElection();
        }
    }

    public void startElection() {
        new JarvisThread(module, this::endElection).delay(votingPeriod).name("Election" + name + "-election").start();
        electionState = ElectionState.VOTING;

        ZonedDateTime dateTime = ZonedDateTime.now();
        String dateString = dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + dateTime.getYear();

        if (registrants.size() <= 2) {
            Jarvis.getJda().getTextChannelById(channelId).sendMessage(dateString + " " + name + " Election cancelled, only " + registrants.size() + " registrants.").queue();
            endElection();
            return;
        }

        FormCreate formCreate = module.createPoll(name, registrants);
        formId = formCreate.form_id;

        Jarvis.getJda().getTextChannelById(channelId).sendMessage(dateString + " " + name + " Election: " + formCreate.form_url).queue();
    }

    public void endElection() {
        electionState = ElectionState.SCHEDULED;

        ArrayList<Registrant> leaderboard = getLeaderboard();
        StringBuilder builder = new StringBuilder();

        for (int x = 0; x < leaderboard.size() && x < 5; x += 1) {
            Registrant registrant = leaderboard.get(x);
            builder.append(registrant.getUser().getName()).append(" (").append(registrant.votes).append(")\n");
        }

        Jarvis.getJda().getTextChannelById(channelId).sendMessage(new EmbedBuilder().setTitle("Results").setColor(Color.GREEN).setDescription(builder).build()).queue();
    }

    public void countVotes() {
        if (formId == null) {
            return;
        }

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
