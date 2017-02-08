package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.noxal.common.util.DateUtils;
import sr.will.jarvis.Jarvis;

import java.util.HashMap;

public class CommandMuteList extends Command {
    private Jarvis jarvis;

    public CommandMuteList(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        String string = "Mutes:```";

        HashMap<String, Long> mutes = jarvis.muteManager.getMutes(message.getGuild().getId());

        for (String userId : mutes.keySet()) {
            Member member = message.getGuild().getMemberById(userId);
            String memberName = userId;
            if (member != null) {
                memberName = member.getEffectiveName();
            }

            string += "\n" + memberName + ": " + DateUtils.formatDateDiff(mutes.get(userId));
        }

        string += "```";

        message.getChannel().sendMessage(string).queue();
    }
}
