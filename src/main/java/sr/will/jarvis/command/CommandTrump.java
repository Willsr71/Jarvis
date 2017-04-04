package sr.will.jarvis.command;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.rest.whatdoestrumpthink.TrumpQuote;

import java.awt.*;

public class CommandTrump extends Command {
    private Jarvis jarvis;

    public CommandTrump(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        Gson gson = new Gson();

        String string = "";
        try {
            string = Unirest.get("https://api.whatdoestrumpthink.com/api/v1/quotes/random").asString().getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
            sendFailureMessage(message, "An error occurred");
        }

        TrumpQuote trumpQuote = gson.fromJson(string, TrumpQuote.class);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("Random Trump Quote", null)
                .setDescription(trumpQuote.message);
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
