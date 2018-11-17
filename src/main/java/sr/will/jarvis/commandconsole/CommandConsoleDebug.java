package sr.will.jarvis.commandconsole;

import sr.will.jarvis.Jarvis;

public class CommandConsoleDebug extends CommandConsole {
    private Jarvis jarvis;

    public CommandConsoleDebug(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void execute(String... args) {
        jarvis.config.debug = !jarvis.config.debug;
        System.out.println("Debug set to " + jarvis.config.debug);
    }
}
