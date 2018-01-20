package sr.will.jarvis.service;

import net.noxal.common.util.Logger;
import sr.will.jarvis.manager.CommandConsoleManager;

public class InputReaderService extends Thread {
    private CommandConsoleManager commandConsoleManager;

    public InputReaderService(CommandConsoleManager commandConsoleManager) {
        this.commandConsoleManager = commandConsoleManager;
    }

    public void run() {
        Logger.info("Input reader thread started");
        while (true) {
            String string;
            string = System.console().readLine();

            if (string != null) {
                commandConsoleManager.executeCommand(string);
            }
        }
    }
}

