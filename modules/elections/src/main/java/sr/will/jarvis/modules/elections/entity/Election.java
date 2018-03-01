package sr.will.jarvis.modules.elections.entity;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
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
    public long roleId;
    public int winnerCount;
    public int dayOfMonth;
    public long votingPeriod;
    public ElectionState electionState;
    public String formId;
    public String formPrefill;
    private ArrayList<Registrant> registrants;

    public Election(ModuleElections module, long guildId, String name, long channelId, long roleId, int winnerCount, int dayOfMonth, long votingPeriod, ElectionState electionState, String formId, String formPrefill, ArrayList<Registrant> registrants) {
        this.module = module;
        this.guildId = guildId;
        this.name = name;
        this.channelId = channelId;
        this.roleId = roleId;
        this.winnerCount = winnerCount;
        this.dayOfMonth = dayOfMonth;
        this.votingPeriod = votingPeriod;
        this.electionState = electionState;
        this.formId = formId;
        this.formPrefill = formPrefill;
        this.registrants = registrants;

        new JarvisThread(module, this::checkShouldStart).executeAt(module.getTomorrow()).repeat(true, 24 * 60 * 60 * 1000).name("Election-" + name).start();
        checkShouldStart();
    }

    public Election(ModuleElections module, long guildId, String name, long channelId, long roleId, int winnerCount, int dayOfMonth, long votingPeriod) {
        this(module, guildId, name, channelId, roleId, winnerCount, dayOfMonth, votingPeriod, ElectionState.SCHEDULED, null, null, new ArrayList<>());
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
        module.updateElectionState(guildId, name, electionState);
        new JarvisThread(module, this::endElection).delay(votingPeriod).name("Election" + name + "-election").start();

        String electionName = getElectionName();

        if (registrants.size() == 0) {
            Jarvis.getJda().getTextChannelById(channelId).sendMessage(electionName + " Election cancelled, only " + registrants.size() + " registrants.").queue();
            endElection();
            return;
        }

        FormCreate formCreate = module.createPoll(electionName, registrants);
        formId = formCreate.form_id;
        formPrefill = formCreate.form_prefill;
        module.updateFormInfo(guildId, name, formId, formPrefill);

        Jarvis.getJda().getTextChannelById(channelId).sendMessage("Election " + electionName + " is starting! To vote, just click the link that " + Jarvis.getJda().getSelfUser().getAsMention() + " sent you!").queue();
        distributePoll();
    }

    public void endElection() {
        electionState = ElectionState.SCHEDULED;
        module.updateElectionState(guildId, name, electionState);

        if (formId != null) {
            FormClose formClose = module.closePoll(formId);
        }

        ArrayList<Registrant> leaderboard = getLeaderboard();

        clearWinnerRole();
        applyWinnerRole(leaderboard);

        StringBuilder builder = new StringBuilder();

        for (int x = 0; x < leaderboard.size() && x < 5; x += 1) {
            Registrant registrant = leaderboard.get(x);
            builder.append(registrant.getUser().getAsMention()).append(" (").append(registrant.votes).append(")\n");
        }

        Jarvis.getJda().getTextChannelById(channelId).sendMessage(new EmbedBuilder().setTitle("Results").setColor(Color.GREEN).setDescription(builder).build()).queue();
    }

    public void distributePoll() {
        Guild guild = Jarvis.getJda().getGuildById(guildId);

        for (Member member : guild.getMembers()) {
            if (member.getUser().getIdLong() == Jarvis.getJda().getSelfUser().getIdLong()) {
                continue;
            }

            member.getUser().openPrivateChannel().queue(this::sendVoteDm);
        }
    }

    public void sendVoteDm(PrivateChannel privateChannel) {
        String authToken = module.getAuthToken(guildId, privateChannel.getUser().getIdLong(), name);
        privateChannel.sendMessage(new EmbedBuilder().setColor(Color.GREEN)
                .setTitle(getElectionName() + " Election")
                .setThumbnail(Jarvis.getJda().getGuildById(guildId).getIconUrl())
                .setFooter("Your token: " + authToken, null)
                .setDescription(formPrefill
                        .replace("DISCORD_DISCRIMINATOR", module.URLEncode(module.getDiscriminator(privateChannel.getUser())))
                        .replace("DISCORD_AUTH_TOKEN", authToken))
                .build()).queue();
    }

    public void countVotes() {
        if (formId == null) {
            return;
        }

        FormGet responses = module.getResponses(formId);
        Guild guild = Jarvis.getJda().getGuildById(guildId);

        HashMap<String, Member> memberTags = new HashMap<>();
        guild.getMembers().forEach((member -> memberTags.put(module.getDiscriminator(member.getUser()), member)));
        HashMap<String, ArrayList<String>> finalVotes = new HashMap<>();

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

            if (finalVotes.containsKey(response.discriminator)) {
                System.out.println("Overriding vote from user " + response.discriminator);
                finalVotes.remove(response.discriminator);
            }
            finalVotes.put(response.discriminator, response.votes);
        }

        finalVotes.values().forEach((votes) -> votes.forEach(this::addVoteByDiscrimimntor));
    }

    public void applyWinnerRole(ArrayList<Registrant> winners) {
        Guild guild = Jarvis.getJda().getGuildById(guildId);
        Role role = guild.getRoleById(roleId);
        for (int x = 0; x < winners.size() && x <= winnerCount; x += 1) {
            Member member = guild.getMember(winners.get(x).getUser());
            guild.getController().addSingleRoleToMember(member, role).queue();
        }
    }

    public void clearWinnerRole() {
        Guild guild = Jarvis.getJda().getGuildById(guildId);
        Role role = guild.getRoleById(roleId);
        for (Member member : guild.getMembersWithRoles(role)) {
            guild.getController().removeSingleRoleFromMember(member, role).queue();
        }
    }

    public String getElectionName() {
        ZonedDateTime dateTime = ZonedDateTime.now();
        String dateString = dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + " " + dateTime.getYear();
        return dateString + " " + name;
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

    public void addRegistrant(long userId) {
        registrants.add(new Registrant(userId));
        module.updateRegistrants(guildId, name, registrants);
    }

    public void removeRegistrant(long userId) {
        registrants.remove(getRegistrantById(userId));
        module.updateRegistrants(guildId, name, registrants);
    }

    public ArrayList<Registrant> getRegistrants() {
        return registrants;
    }
}
