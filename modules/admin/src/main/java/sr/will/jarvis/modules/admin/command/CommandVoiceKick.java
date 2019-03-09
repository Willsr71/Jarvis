package sr.will.jarvis.modules.admin.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.admin.ModuleAdmin;

public class CommandVoiceKick extends Command {
    private ModuleAdmin module;

    public CommandVoiceKick(ModuleAdmin module) {
        super("voicekick", "voicekick <user mention|user id>", "Kicks the specified memver from voice chat", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkBotPermission(message, Permission.VOICE_MOVE_OTHERS);
        checkBotPermission(message, Permission.MANAGE_CHANNEL);
        checkUserPermission(message, Permission.VOICE_MUTE_OTHERS);

        User user = getMentionedUser(message, args);
        if (user == null) {
            sendFailureMessage(message, "No user tagged");
            return;
        }

        Guild guild = message.getGuild();
        Member member = guild.getMember(user);
        if (member == null) {
            sendFailureMessage(message, "User is not a member of this guild");
            return;
        }

        if (!member.getVoiceState().inVoiceChannel()) {
            sendFailureMessage(message, "User is not in voice");
            return;
        }

        guild.getController().createVoiceChannel("JarvisKick").queue(channel -> {
            guild.getController().moveVoiceMember(member, (VoiceChannel) channel).queue(success -> {
                channel.delete().queue();
            });
        });
    }
}
