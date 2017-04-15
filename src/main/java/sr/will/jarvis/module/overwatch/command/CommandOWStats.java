package sr.will.jarvis.module.overwatch.command;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.module.overwatch.ModuleOverwatch;
import sr.will.jarvis.rest.owapi.UserStats;

import java.awt.*;

public class CommandOWStats extends Command {
    private ModuleOverwatch module;

    public CommandOWStats(ModuleOverwatch module) {
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        Gson gson = new Gson();

        if (args.length == 0) {
            sendFailureMessage(message, "No battletag specified");
            return;
        }

        String string;
        try {
            string = Unirest.get("https://owapi.net/api/v3/u/" + args[0].replace("#", "-") + "/stats").asString().getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
            sendFailureMessage(message, "An error occurred");
            return;
        }

        System.out.println(string);

        UserStats userStats = gson.fromJson(string, UserStats.class);

        if (userStats.error != null) {
            sendFailureMessage(message, userStats.msg);
            return;
        }

        UserStats.Region.Stats.Mode.OverallStats overallStats = userStats.getRegion().stats.quickplay.overall_stats;

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle(args[0], null)
                .addField("Level", ((overallStats.prestige * 100) + overallStats.level) + "", true)
                .addField("SR", overallStats.comprank + "", true)
                .setThumbnail(overallStats.avatar);
        message.getChannel().sendMessage(embed.build()).queue();
    }
}
