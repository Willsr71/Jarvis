package sr.will.jarvis.module.chatbot;

import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.chatbot.command.CommandBotAdd;
import sr.will.jarvis.module.chatbot.command.CommandBotRemove;
import sr.will.jarvis.module.chatbot.event.EventHandlerChatBot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ModuleChatBot extends Module {
    private Jarvis jarvis;

    private ChatterBotFactory botFactory = new ChatterBotFactory();
    private HashMap<Long, ChatterBotSession> chatterBots = new HashMap<>();

    public ModuleChatBot(Jarvis jarvis) {
        super(
                "ChatBot",
                "An interactive chatbot and commands",
                new ArrayList<>(Arrays.asList(
                        Permission.MESSAGE_READ,
                        Permission.MESSAGE_WRITE
                )),
                false
        );
        this.jarvis = jarvis;

        jarvis.eventManager.registerHandler(new EventHandlerChatBot(this));

        jarvis.commandManager.registerCommand("botadd", new CommandBotAdd(this));
        jarvis.commandManager.registerCommand("botremove", new CommandBotRemove(this));
    }

    @Override
    public void finishStart() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void reload() {

    }

    public void addBot(long channelId) {
        Jarvis.getDatabase().execute("INSERT INTO chatterbot_channels (channel) VALUES (?);", channelId);

        createBot(channelId);
    }

    public void removeBot(long channelId) {
        Jarvis.getDatabase().execute("DELETE FROM chatterbot_channels WHERE (channel = ?);", channelId);

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

        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT 1 FROM chatterbot_channels WHERE (channel = ?) LIMIT 1;", channelId);
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
