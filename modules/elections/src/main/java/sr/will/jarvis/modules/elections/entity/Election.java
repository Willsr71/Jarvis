package sr.will.jarvis.modules.elections.entity;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.modules.elections.ModuleElections;
import sr.will.jarvis.modules.elections.rest.formManager.FormClose;
import sr.will.jarvis.modules.elections.rest.formManager.FormCreate;
import sr.will.jarvis.modules.elections.rest.formManager.FormGet;
import sr.will.jarvis.thread.JarvisThread;

import java.awt.*;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
        if (electionState == ElectionState.VOTING) {
            System.out.println("already voting");
            return;
        }

        electionState = ElectionState.VOTING;
        new JarvisThread(module, this::endElection).delay(votingPeriod).name("Election" + name + "-election").start();

        ZonedDateTime dateTime = ZonedDateTime.now();
        String dateString = dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + dateTime.getYear();
        String electionName = dateString + " " + name;

        if (registrants.size() == 0) {
            Jarvis.getJda().getTextChannelById(channelId).sendMessage(electionName + " Election cancelled, only " + registrants.size() + " registrants.").queue();
            endElection();
            return;
        }

        FormCreate formCreate = module.createPoll(electionName, registrants);
        formId = formCreate.form_id;
        module.updateFormId(guildId, name, formId);

        Jarvis.getJda().getTextChannelById(channelId).sendMessage("Election " + electionName + " is starting! To vote, just click the link that " + Jarvis.getJda().getSelfUser().getAsMention() + " sent you!").queue();
        distributePoll(electionName, formCreate.form_prefill);
    }

    public void endElection() {
        electionState = ElectionState.SCHEDULED;

        FormClose formClose = module.closePoll(formId);

        ArrayList<Registrant> leaderboard = getLeaderboard();
        StringBuilder builder = new StringBuilder();

        for (int x = 0; x < leaderboard.size() && x < 5; x += 1) {
            Registrant registrant = leaderboard.get(x);
            builder.append(registrant.getUser().getName()).append(" (").append(registrant.votes).append(")\n");
        }

        Jarvis.getJda().getTextChannelById(channelId).sendMessage(new EmbedBuilder().setTitle("Results").setColor(Color.GREEN).setDescription(builder).build()).queue();
    }

    public void distributePoll(String electionName, String formPrefill) {
        Guild guild = Jarvis.getJda().getGuildById(guildId);

        for (Member member : guild.getMembers()) {
            if (member.getUser().getIdLong() == Jarvis.getJda().getSelfUser().getIdLong()) {
                continue;
            }

            /* TEMP
            if (member.getUser().getIdLong() != 112587845968912384L) {
                continue;
            }*/

            member.getUser().openPrivateChannel().queue((privateChannel) -> {
                String authToken = module.getAuthToken(guildId, member.getUser().getIdLong(), name);
                privateChannel.sendMessage(new EmbedBuilder().setColor(Color.GREEN)
                        .setTitle(electionName + " Election")
                        .setThumbnail(guild.getIconUrl())
                        .setFooter("Your token: " + authToken, null)
                        .setDescription(formPrefill
                                .replace("DISCORD_DISCRIMINATOR", module.URLEncode(module.getDiscriminator(member.getUser())))
                                .replace("DISCORD_AUTH_TOKEN", authToken))
                        .build()).queue();
            });
        }
    }

    public void countVotes() {
        if (formId == null) {
            return;
        }

        FormGet responses = module.getResponses(formId);
        Guild guild = Jarvis.getJda().getGuildById(guildId);

        HashMap<String, Member> memberTags = new HashMap<>();
        guild.getMembers().forEach((member -> memberTags.put(module.getDiscriminator(member.getUser()), member)));

        for (FormGet.Response response : responses.responses) {
            System.out.println(response.discriminator);
            if (!memberTags.containsKey(response.discriminator)) {
                System.out.println("User " + response.discriminator + " thrown out, does not exist on guild");
                continue;
            }

            if (!response.auth_token.equals(module.getAuthToken(guildId, memberTags.get(response.discriminator).getUser().getIdLong(), name))) {
                System.out.println("Tokens for user " + response.discriminator + " do not match");
                continue;
            }

            response.votes.forEach(this::addVoteByDiscrimimntor);
        }
    }

    public ArrayList<Registrant> getLeaderboard() {
        countVotes();

        registrants.sort(Comparator.comparingInt(Registrant::getVotes).reversed());
        return registrants;
    }

    public Registrant getRegistrantByDiscriminator(String discriminator) {
        for (Registrant registrant : registrants) {
            if (module.getDiscriminator(registrant.getUser()).equals(discriminator)) {
                return registrant;
            }
        }

        return null;
    }

    public void addVoteByDiscrimimntor(String discriminator) {
        Registrant registrant = getRegistrantByDiscriminator(discriminator);
        if (registrant == null) {
            System.out.println("registrant is null!?");
            return;
        }

        registrant.addVote();
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
