package sr.will.jarvis.manager;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.*;
import sr.will.jarvis.exception.BotPermissionException;
import sr.will.jarvis.exception.ModuleNotEnabledException;
import sr.will.jarvis.exception.UserPermissionException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static sr.will.jarvis.command.Command.encodeString;

public class CommandManager {
    private Jarvis jarvis;

    private HashMap<String, Command> commands = new HashMap<>();

    public CommandManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public ArrayList<String> getCommands() {
        return new ArrayList<>(commands.keySet());
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public void registerCommands() {
        registerCommand("clear", new CommandClear(jarvis));
        registerCommand("commandadd", new CommandCommandAdd(jarvis));
        registerCommand("commandremove", new CommandCommandRemove(jarvis));
        registerCommand("emoji", new CommandEmoji(jarvis));
        registerCommand("emote", new CommandEmote(jarvis));
        registerCommand("google", new CommandGoogle(jarvis));
        registerCommand("help", new CommandHelp(jarvis));
        registerCommand("moduledisable", new CommandModuleDisable(jarvis));
        registerCommand("moduleenable", new CommandModuleEnable(jarvis));
        registerCommand("modules", new CommandModules(jarvis));
        registerCommand("restart", new CommandRestart(jarvis));
        registerCommand("stats", new CommandStats(jarvis));
    }

    public void addCustomCommand(String guildId, String command, String response) {
        jarvis.database.execute("INSERT INTO custom_commands (guild, command, response) VALUES (?, ?, ?);", guildId, command, encodeString(response));
    }

    public void removeCustomCommand(String guildId, String command) {
        jarvis.database.execute("DELETE FROM custom_commands WHERE (guild = ? AND command = ?);", guildId, command);
    }

    public String getCustomCommandResponse(String guildId, String command) {
        try {
            ResultSet result = jarvis.database.executeQuery("SELECT response FROM custom_commands WHERE (guild = ? AND command = ?) LIMIT 1;", guildId, command);
            if (result.first()) {
                return Command.decodeString(result.getString("response"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<String> getCustomCommandsByGuild(String guildId) {
        ArrayList<String> commandList = new ArrayList<>();

        try {
            ResultSet result = jarvis.database.executeQuery("SELECT command FROM custom_commands WHERE (guild = ?);", guildId);
            while (result.next()) {
                commandList.add(result.getString("command"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commandList;
    }

    public void executeCommand(Message message) {
        String string = message.getRawContent().substring(1);

        if (string.equals("")) {
            return;
        }

        List<String> parts = Arrays.asList(string.split(" "));

        System.out.println(parts);

        String command = parts.get(0).toLowerCase();
        String[] args = parts.subList(1, parts.size()).toArray(new String[parts.size() - 1]);

        executeCommand(command, message, args);
    }

    public void executeCommand(String command, Message message, String... args) {
        if (commands.containsKey(command)) {
            try {
                commands.get(command).execute(message, args);
            } catch (BotPermissionException | UserPermissionException | ModuleNotEnabledException e) {
                System.out.println(e.getMessage());
            }
            return;
        }

        String customCommandResponse = getCustomCommandResponse(message.getGuild().getId(), command);

        if (customCommandResponse != null) {
            message.getChannel().sendMessage(customCommandResponse).queue();
        }
    }
}
