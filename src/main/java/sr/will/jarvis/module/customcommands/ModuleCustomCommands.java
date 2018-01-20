package sr.will.jarvis.module.customcommands;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.customcommands.command.CommandCommandAdd;
import sr.will.jarvis.module.customcommands.command.CommandCommandRemove;
import sr.will.jarvis.module.customcommands.command.CommandCommands;
import sr.will.jarvis.module.customcommands.event.EventHandlerCustomCommands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class ModuleCustomCommands extends Module {

    public ModuleCustomCommands() {
        super(
                "CustomCommands",
                "Custom commands and responses",
                new ArrayList<>(Arrays.asList(
                        Permission.MESSAGE_READ,
                        Permission.MESSAGE_WRITE,
                        Permission.MESSAGE_ADD_REACTION
                )),
                true
        );

        registerEventHandler(new EventHandlerCustomCommands(this));

        registerCommand("commandadd", new CommandCommandAdd(this));
        registerCommand("commandremove", new CommandCommandRemove(this));
        registerCommand("commands", new CommandCommands(this));
    }


    @Override
    public void finishStart() {
        Jarvis.getDatabase().execute("CREATE TABLE IF NOT EXISTS custom_commands(" +
                "id int NOT NULL AUTO_INCREMENT," +
                "guild bigint(20) NOT NULL," +
                "command varchar(255) NOT NULL," +
                "response text NOT NULL," +
                "PRIMARY KEY (id));");
    }

    @Override
    public void stop() {

    }

    @Override
    public void reload() {

    }

    public void processCustomCommand(Message message) {
        if (!isEnabled(message.getGuild().getIdLong())) {
            return;
        }

        String string = message.getContentRaw().substring(1);
        if (string.equals("")) {
            return;
        }

        String customCommandResponse = getCustomCommandResponse(message.getGuild().getIdLong(), string.toLowerCase().split(" ")[0]);

        if (customCommandResponse != null) {
            message.getChannel().sendMessage(customCommandResponse).queue();
        }
    }

    public void addCustomCommand(long guildId, String command, String response) {
        Jarvis.getDatabase().execute("INSERT INTO custom_commands (guild, command, response) VALUES (?, ?, ?);", guildId, command, response);
    }

    public void removeCustomCommand(long guildId, String command) {
        Jarvis.getDatabase().execute("DELETE FROM custom_commands WHERE (guild = ? AND command = ?);", guildId, command);
    }

    public String getCustomCommandResponse(long guildId, String command) {
        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT response FROM custom_commands WHERE (guild = ? AND command = ?) LIMIT 1;", guildId, command);
            if (result.first()) {
                return result.getString("response");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<String> getCustomCommandsByGuild(long guildId) {
        ArrayList<String> commandList = new ArrayList<>();

        try {
            ResultSet result = Jarvis.getDatabase().executeQuery("SELECT command FROM custom_commands WHERE (guild = ?);", guildId);
            while (result.next()) {
                commandList.add(result.getString("command"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commandList;
    }
}
