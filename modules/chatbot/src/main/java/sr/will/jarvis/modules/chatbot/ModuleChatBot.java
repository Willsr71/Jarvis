package sr.will.jarvis.modules.chatbot;

import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.chatbot.command.CommandBotAdd;
import sr.will.jarvis.modules.chatbot.command.CommandBotRemove;
import sr.will.jarvis.modules.chatbot.event.EventHandlerChatBot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ModuleChatBot extends Module {
    private ChatterBotFactory botFactory = new ChatterBotFactory();
    private HashMap<Long, ChatterBotSession> chatterBots = new HashMap<>();

    public void initialize() {
        setNeededPermissions(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE
        );
        setDefaultEnabled(false);

        registerEventHandler(new EventHandlerChatBot(this));

        registerCommand("botadd", new CommandBotAdd(this));
        registerCommand("botremove", new CommandBotRemove(this));
    }

    public void finishStart() {
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS chatterbot_channels(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "channel bigint(20) NOT NULL," +
                "PRIMARY KEY (id));");
    }

    public void stop() {

    }

    public void reload() {

    }

    public void addBot(long channelId) {
        Jarvis.getDatabase().execute("INSERT INTO chatterbot_channels (channel) VALUES (?);", channelId);
        new CachedChatterbotChannel(channelId, true);

        createBot(channelId);
    }

    public void removeBot(long channelId) {
        Jarvis.getDatabase().execute("DELETE FROM chatterbot_channels WHERE (channel = ?);", channelId);
        new CachedChatterbotChannel(channelId, false);

        chatterBots.remove(channelId);
    }

    public void createBot(long channelId) {
        try {
            chatterBots.put(channelId, botFactory.create(ChatterBotType.PANDORABOTS, "a847934aae3456cb").createSession());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isBotChannel(long channelId) {
        if (chatterBots.containsKey(channelId)) {
            return true;
        }

        CachedChatterbotChannel c = CachedChatterbotChannel.getEntry(channelId);
        if (c != null) {
            return c.isChatterbotChannel();
        }

        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT 1 FROM chatterbot_channels WHERE (channel = ?) LIMIT 1;", channelId);
            new CachedChatterbotChannel(channelId, result.first());
            if (result.first()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void sendResponse(Message message) {
        if (!chatterBots.containsKey(message.getChannel().getIdLong())) {
            createBot(message.getChannel().getIdLong());
        }

        if (message.getContentDisplay().isEmpty()) {
            return;
        }

        try {
            message.getChannel().sendMessage(chatterBots.get(message.getChannel().getIdLong()).think(message.getContentDisplay())).queue();
        } catch (Exception e) {
            message.getChannel().sendMessage("Error: " + e.getMessage()).queue();
            e.printStackTrace();
        }
    }
}
