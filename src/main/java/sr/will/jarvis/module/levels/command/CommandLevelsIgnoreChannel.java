package sr.will.jarvis.module.levels.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.levels.ModuleLevels;

public class CommandLevelsIgnoreChannel extends Command {
    private ModuleLevels module;

    public CommandLevelsIgnoreChannel(ModuleLevels module) {
        super("levelsignorechannel", "levelsignorechannel [channel]", "Ignore or stop ignoring a channel for xp gain", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        checkUserPermission(message, Permission.MANAGE_CHANNEL);

        MessageChannel channel = message.getChannel();
        if (getMentionedChannel(message, args) != null) {
            channel = getMentionedChannel(message, args);
        }

        if (module.channelIgnored(channel.getIdLong())) {
            module.unignoreChannel(channel.getIdLong());
            sendSuccessMessage(message, "Channel " + channel.getName() + " now affects xp gain", false);
        } else {
            module.ignoreChannel(channel.getIdLong());
            sendSuccessMessage(message, "Channel " + channel.getName() + " no longer affects xp gain", false);
        }
    }
}
