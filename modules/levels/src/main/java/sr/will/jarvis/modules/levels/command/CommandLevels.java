package sr.will.jarvis.modules.levels.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.image.ImageUtilities;
import sr.will.jarvis.modules.levels.ModuleLevels;
import sr.will.jarvis.modules.levels.XPUser;
import sr.will.jarvis.modules.levels.image.ImageMaker;

import java.io.IOException;
import java.util.ArrayList;

public class CommandLevels extends Command {
    private ModuleLevels module;

    public CommandLevels(ModuleLevels module) {
        super("leaderboard", "leaderboard [page]", "Displays the experience leaderboard of the members of the guild", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);
        ArrayList<XPUser> leaderboard = module.getLeaderboard(message.getGuild().getIdLong());

        int page = 1;
        if (args.length != 0) {
            page = Integer.valueOf(args[0]);
            if (page <= 0 || page > Math.ceil(leaderboard.size() / 10D)) {
                sendFailureMessage(message, "No such page");
                return;
            }
        }
        page = page - 1;

        ArrayList<XPUser> displayedBoard = new ArrayList<>(leaderboard.subList(page * 10, Math.min(leaderboard.size(), (page + 1) * 10)));

        try {
            ImageUtilities.sendImage(message.getChannel(), ImageMaker.createLeaderboardImage(displayedBoard, page, leaderboard.get(0).xp, leaderboard.size()), "leaderboard.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
