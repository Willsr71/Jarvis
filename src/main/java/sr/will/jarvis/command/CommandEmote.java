package sr.will.jarvis.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

import java.awt.*;

public class CommandEmote extends Command {
    private Jarvis jarvis;

    public CommandEmote(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        if (message.getEmotes().size() == 0) {
            message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error", "https://jarvis.will.sr").setColor(Color.RED).setDescription("You must include an emote").build()).queue();
            return;
        }

        Emote emote = message.getEmotes().get(0);
        EmbedBuilder embed = new EmbedBuilder().setTitle("Emote Info", "https://jarvis.will.sr").setColor(Color.GREEN);

        embed.addField("Emote", emote.getAsMention(), false);
        embed.addField("Name", emote.getName(), false);
        embed.addField("ID", emote.getId(), false);
        embed.addField("Image URL", emote.getImageUrl(), false);

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
