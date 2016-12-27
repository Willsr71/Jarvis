package sr.will.jarvis.manager;

import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ChatterBotManager {
    private Jarvis jarvis;

    private ChatterBotFactory botFactory = new ChatterBotFactory();
    private HashMap<String, ChatterBotSession> chatterBots = new HashMap<>();

    public ChatterBotManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void addBot(String channelId) {
        jarvis.database.execute("INSERT INTO chatterbot_channels (channel) VALUES (?);", channelId);

        createBot(channelId);
    }

    public void removeBot(String channelId) {
        jarvis.database.execute("DELETE FROM chatterbot_channels WHERE (channel = ?);", channelId);

        chatterBots.remove(channelId);
    }

    public void createBot(String channelId) {
        try {
            chatterBots.put(channelId, botFactory.create(ChatterBotType.CLEVERBOT).createSession());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isBotChannel(String channelId) {
        if (chatterBots.containsKey(channelId)) {
            return true;
        }

        try {
            ResultSet result = jarvis.database.executeQuery("SELECT 1 FROM chatterbot_channels WHERE (channel = ?) LIMIT 1;", channelId);
            if (result.first()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void sendResponse(Message message) {
        if (!chatterBots.containsKey(message.getChannel().getId())) {
            createBot(message.getChannel().getId());
        }

        try {
            message.getChannel().sendMessage(chatterBots.get(message.getChannel().getId()).think(message.getContent())).queue();
        } catch (Exception e) {
            message.getChannel().sendMessage("Error: " + e.getMessage()).queue();
            e.printStackTrace();
        }
    }
}
