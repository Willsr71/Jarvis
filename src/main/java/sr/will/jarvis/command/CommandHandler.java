package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandHandler {
    private HashMap<String, Command> commands = new HashMap<>();

    public void registerCommand(String command, Command commandClass) {
        commands.put(command, commandClass);
    }

    public void unregisterCommand(String command) {
        commands.remove(command);
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
    }
}
