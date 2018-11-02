package sr.will.jarvis.modules.levels.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.levels.ModuleLevels;

public class CommandLevelsIgnoreChannel extends Command {
    private ModuleLevels module;

    public CommandLevelsIgnoreChannel(ModuleLevels module) {
        super("levelsignorechannel", "levelsignorechannel [channelId]", "Ignore or stop ignoring a channelId for xp gain", module);
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
            sendSuccessMessage(message, "Channel " + channel.getName() + " now affects xp gain");
        } else {
            module.ignoreChannel(channel.getIdLong());
            sendSuccessMessage(message, "Channel " + channel.getName() + " no longer affects xp gain");
        }
    }
}
