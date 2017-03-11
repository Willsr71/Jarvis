package sr.will.jarvis.manager;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Thread.sleep;

public class MuteManager {
    private Jarvis jarvis;
    private boolean running = true;
    private ArrayList<Thread> unmuteThreads = new ArrayList<>();

    public MuteManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public HashMap<String, Long> getMutes(String guildId) {
        HashMap<String, Long> mutes = new HashMap<>();
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT user, duration FROM mutes WHERE (guild = ?);", guildId);
            while (result.next()) {
                mutes.put(result.getString("user"), result.getLong("duration"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mutes;
    }

    public long getMuteDuration(String guildId, String userId) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT duration FROM mutes WHERE (guild = ? AND user = ?) ORDER BY id DESC LIMIT 1;", guildId, userId);
            if (result.first()) {
                return result.getLong("duration");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean isMuted(String guildId, String userId) {
        return DateUtils.timestampApplies(getMuteDuration(guildId, userId));
    }

    public void mute(String guildId, String userId, String invokerId) {
        mute(guildId, userId, invokerId, -1);
    }

    public void mute(String guildId, String userId, String invokerId, long duration) {
        jarvis.database.execute("INSERT INTO mutes (guild, user, invoker, duration) VALUES (?, ?, ?, ?)", guildId, userId, invokerId, duration);
        setMuted(guildId, userId, true);
        startUnmuteThread(guildId, userId, duration);

        /*
        jarvis.getJda().getUserById(userId).openPrivateChannel().queue((privateChannel -> {
            privateChannel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Muted", "https://jarvis.will.sr")
                            .setColor(Color.RED)
                            .setDescription("You have been muted in " + jarvis.getJda().getGuildById(guildId).getName() + " for " + DateUtils.formatDateDiff(duration))
                            .build())
                    .queue();
        }));
        */
    }

    public void unmute(String guildId, String userId) {
        jarvis.database.execute("DELETE FROM mutes WHERE (guild = ? AND user = ? );", guildId, userId);
        setMuted(guildId, userId, false);

        jarvis.getJda().getUserById(userId).openPrivateChannel().queue((privateChannel -> {
            privateChannel.sendMessage(
                    new EmbedBuilder()
                            .setTitle("Unmuted", "https://jarvis.will.sr")
                            .setColor(Color.GREEN)
                            .setDescription("You have been unmuted in " + jarvis.getJda().getGuildById(guildId).getName())
                            .build())
                    .queue();
        }));
    }

    public void setup() {
        for (Guild guild : jarvis.getJda().getGuilds()) {
            System.out.println("Setting up guild " + guild.getName());
            try {
                deleteOldRoles(guild);
                createMuteRole(guild);
            } catch (PermissionException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
        for (Thread thread : unmuteThreads) {
            thread.interrupt();
        }
    }

    public void deleteOldRoles(Guild guild) {
        List<Role> roles = new ArrayList<>();
        roles.addAll(guild.getRolesByName("Jarvis_Mute", true));
        roles.addAll(guild.getRolesByName("new role", true));
        for (Role role : roles) {
            role.delete().queue(aVoid -> {
                System.out.println("Deleted role " + role.getName() + " from guild " + role.getGuild().getName());
            });
        }
    }

    public void createMuteRole(Guild guild) {
        guild.getController().createRole().queue((role) -> {
            role.getManager().setName("Jarvis_Mute").queue(aVoid -> {
                role.getManager().setPermissions().queue(aVoid1 -> {
                    role.getManager().setColor(Color.black).queue(aVoid2 -> {
                        role.getManager().setMentionable(false).queue(aVoid3 -> {
                            addMuteRoleToChannels(guild, role);
                            System.out.println("Created mute role in guild " + guild.getName());
                            processMutedMembers(guild, role);
                        });
                    });
                });
            });
        });
    }

    public void addMuteRoleToChannels(Guild guild, Role role) {
        List<TextChannel> channels = guild.getTextChannels();

        for (TextChannel channel : channels) {
            channel.createPermissionOverride(role).queue(aVoid -> {
                channel.getPermissionOverride(role).getManager().deny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).queue();
            });
        }
    }

    public void processMutedMembers(Guild guild, Role role) {
        HashMap<String, Long> mutes = jarvis.muteManager.getMutes(guild.getId());

        System.out.println("Processing " + mutes.size() + " muted members for " + guild.getName());

        for (String userId : mutes.keySet()) {
            if (!DateUtils.timestampApplies(mutes.get(userId))) {
                unmute(guild.getId(), userId);
                continue;
            }

            setMuted(guild, guild.getMemberById(userId), role, true);
            startUnmuteThread(guild.getId(), userId, mutes.get(userId));
        }
    }

    public void setMuted(String guildId, String userId, boolean applied) {
        Guild guild = jarvis.getJda().getGuildById(guildId);

        setMuted(guild, guild.getMemberById(userId), guild.getRolesByName("Jarvis_Mute", true).get(0), applied);
    }

    public void setMuted(Guild guild, Member member, Role role, boolean applied) {
        if (applied) {
            guild.getController().addRolesToMember(member, role).queue();
        } else {
            guild.getController().removeRolesFromMember(member, role).queue();
        }
    }

    public void startUnmuteThread(final String guildId, final String userId, final long duration) {
        startThread(() -> {
            try {
                long sleepTime = duration - System.currentTimeMillis();
                System.out.println("Sleeping for " + sleepTime);
                sleep(sleepTime);
                Jarvis.getInstance().muteManager.unmute(guildId, userId);
            } catch (InterruptedException e) {
                if (running) {
                    e.printStackTrace();
                    startUnmuteThread(guildId, userId, duration);
                }
                System.out.println("Stopping thread!");
            }
        });
    }

    public void startThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.run();
        unmuteThreads.add(thread);
    }
}
