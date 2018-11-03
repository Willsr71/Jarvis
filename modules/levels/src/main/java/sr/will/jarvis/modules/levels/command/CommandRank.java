package sr.will.jarvis.modules.levels.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.image.ImageUtilities;
import sr.will.jarvis.modules.levels.ModuleLevels;
import sr.will.jarvis.modules.levels.XPUser;
import sr.will.jarvis.modules.levels.image.ImageMaker;

import java.awt.*;
import java.io.IOException;

public class CommandRank extends Command {
    private ModuleLevels module;

    public CommandRank(ModuleLevels module) {
        super("rank", "rank [user mention|user id]", "Displays the experience info about the mentioned member", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        User user = getMentionedUser(message, args);
        if (user == null) {
            user = message.getAuthor();
        }

        if (!module.userExists(message.getGuild().getIdLong(), user.getIdLong())) {
            module.addUser(message.getGuild().getIdLong(), user.getIdLong(), 0);
        }

        EmbedBuilder embed = new EmbedBuilder().setColor(Color.GREEN);
        embed.setAuthor(message.getGuild().getMember(user).getEffectiveName(), null, user.getEffectiveAvatarUrl());

        if (module.getUserXp(message.getGuild().getIdLong(), user.getIdLong()) < 0) {
            embed.setDescription("User has opted out of levels");
            message.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        XPUser xpUser = module.getXPUser(message.getGuild().getIdLong(), user.getIdLong());

        try {
            ImageUtilities.sendImage(message.getChannel(), ImageMaker.createRankImage(xpUser), "rank.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
