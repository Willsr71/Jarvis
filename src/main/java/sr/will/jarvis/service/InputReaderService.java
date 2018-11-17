package sr.will.jarvis.service;

import sr.will.jarvis.Jarvis;
import sr.will.jarvis.manager.CommandConsoleManager;

public class InputReaderService extends Thread {
    private CommandConsoleManager commandConsoleManager;

    public InputReaderService(CommandConsoleManager commandConsoleManager) {
        this.commandConsoleManager = commandConsoleManager;
    }

    public void run() {
        setName("InputReader");
        Jarvis.getLogger().info("Input reader thread started");
        while (true) {
            String string;
            string = System.console().readLine();

            if (string != null) {
                commandConsoleManager.executeCommand(string);
            }
        }
    }
}

