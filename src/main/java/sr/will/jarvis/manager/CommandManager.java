package sr.will.jarvis.manager;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.*;
import sr.will.jarvis.exception.BotPermissionException;
import sr.will.jarvis.exception.ModuleNotEnabledException;
import sr.will.jarvis.exception.UserPermissionException;
import sr.will.jarvis.module.Module;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

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

    public ArrayList<String> getCommandsByModule(Module module) {
        ArrayList<String> commandList = new ArrayList<>();

        for (String command : commands.keySet()) {
            if (getCommand(command).getModule() == module) {
                commandList.add(command);
            }
        }

        return commandList;
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public void registerCommands() {
        registerCommand("clear", new CommandClear(jarvis));
        registerCommand("commandadd", new CommandCommandAdd(jarvis));
        registerCommand("commandremove", new CommandCommandRemove(jarvis));
        registerCommand("define", new CommandDefine(jarvis));
        registerCommand("google", new CommandGoogle(jarvis));
        registerCommand("help", new CommandHelp(jarvis));
        registerCommand("invite", new CommandInvite(jarvis));
        registerCommand("moduledisable", new CommandModuleDisable(jarvis));
        registerCommand("moduleenable", new CommandModuleEnable(jarvis));
        registerCommand("modules", new CommandModules(jarvis));
        registerCommand("restart", new CommandRestart(jarvis));
        registerCommand("source", new CommandSource());
        registerCommand("stats", new CommandStats(jarvis));
    }

    public void addCustomCommand(long guildId, String command, String response) {
        jarvis.database.execute("INSERT INTO custom_commands (guild, command, response) VALUES (?, ?, ?);", guildId, command, response);
    }

    public void removeCustomCommand(long guildId, String command) {
        jarvis.database.execute("DELETE FROM custom_commands WHERE (guild = ? AND command = ?);", guildId, command);
    }

    public String getCustomCommandResponse(long guildId, String command) {
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

    public ArrayList<String> getCustomCommandsByGuild(long guildId) {
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
            } catch (Exception e) {
                message.getChannel().sendMessage(new EmbedBuilder().setTitle("Error!", null).setColor(Color.RED).setDescription(e.toString()).build()).queue();
                e.printStackTrace();
            }

            System.out.println(String.format("%s | %s | %s | %s", message.getGuild().getId(), message.getAuthor().getId(), command, Arrays.toString(args)));
            return;
        }

        String customCommandResponse = getCustomCommandResponse(message.getGuild().getIdLong(), command);

        if (customCommandResponse != null) {
            message.getChannel().sendMessage(customCommandResponse).queue();
        }
    }
}
