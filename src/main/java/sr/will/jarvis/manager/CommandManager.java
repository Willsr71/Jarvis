package sr.will.jarvis.manager;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandManager {
    private Jarvis jarvis;

    private HashMap<String, Command> commands = new HashMap<>();

    public CommandManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void registerCommand(String command, Command commandClass) {
        commands.put(command, commandClass);
    }

    public void addCustomCommand(String guildId, String command, String response) {
        jarvis.database.execute("INSERT INTO custom_commands (guild, command, response) VALUES (?, ?, ?);", guildId, command, response);
    }

    public void removeCustomCommand(String guildId, String command) {
        jarvis.database.execute("DELETE FROM custom_commands WHERE (guild = ? AND command = ?);", guildId, command);
    }

    public String getCustomCommandResponse(String guildId, String command) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT response FROM custom_commands WHERE (guild = ? AND command = ?) LIMIT 1;", guildId, command);
            if (result.first()) {
                return result.getString("response");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void executeCommand(Message message) {
        String string = message.getContent().substring(1);

        if (string.equals("")) {
            return;
        }

        string = string.toLowerCase();
        List<String> parts = Arrays.asList(string.split(" "));

        System.out.println(parts);

        String command = parts.get(0);
        String[] args = parts.subList(1, parts.size()).toArray(new String[parts.size() - 1]);

        executeCommand(command, message, args);
    }

    public void executeCommand(String command, Message message, String... args) {
        if (commands.containsKey(command)) {
            commands.get(command).execute(message, args);
            return;
        }

        String customCommandResponse = getCustomCommandResponse(message.getGuild().getId(), command);

        if (customCommandResponse != null) {
            message.getChannel().sendMessage(customCommandResponse).queue();
        }
    }
}
