package sr.will.jarvis.manager;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.commandconsole.CommandConsole;
import sr.will.jarvis.commandconsole.CommandConsoleDebug;
import sr.will.jarvis.commandconsole.CommandConsolePID;
import sr.will.jarvis.commandconsole.CommandConsoleStop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandConsoleManager {
    private Jarvis jarvis;

    private HashMap<String, CommandConsole> commands = new HashMap<>();

    public CommandConsoleManager(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void registerCommand(String name, CommandConsole command) {
        commands.put(name, command);
    }

    public void registerCommands() {
        registerCommand("debug", new CommandConsoleDebug(jarvis));
        registerCommand("pid", new CommandConsolePID(jarvis));
        registerCommand("stop", new CommandConsoleStop(jarvis));
    }

    public void executeCommand(String string) {
        if (string.equals("")) {
            return;
        }

        string = string.toLowerCase();
        List<String> parts = Arrays.asList(string.split(" "));

        String command = parts.get(0);
        String[] args = parts.subList(1, parts.size()).toArray(new String[parts.size() - 1]);

        executeCommand(command, args);
    }

    public void executeCommand(String command, String... args) {
        if (commands.containsKey(command)) {
            commands.get(command).execute(args);
            return;
        }

        System.out.println("Command does not exist.");
    }
}
