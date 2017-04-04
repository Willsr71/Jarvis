package sr.will.jarvis.command;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.rest.urbandictionary.Definition;

import java.awt.*;

public class CommandDefine extends Command {
    private Jarvis jarvis;

    public CommandDefine(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    @Override
    public void execute(Message message, String... args) {
        Gson gson = new Gson();

        if (args.length == 0) {
            sendFailureMessage(message, "No word defined");
            return;
        }

        String string = "";

        try {
            string = Unirest.get("http://api.urbandictionary.com/v0/define?term=" + condenseArgs("+", args)).asString().getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
            sendFailureMessage(message, "An error occurred");
        }

        Definition definition = gson.fromJson(string, Definition.class);

        if (definition.list.size() == 0) {
            sendFailureMessage(message, "Word not defined");
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle(definition.list.get(0).word, "https://www.urbandictionary.com/define.php?term=" + condenseArgs("+", args))
                .setDescription(definition.list.get(0).definition + "\n\n_" + definition.list.get(0).example + "_");
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
