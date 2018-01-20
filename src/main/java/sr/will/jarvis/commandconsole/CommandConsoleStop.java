package sr.will.jarvis.commandconsole;

import sr.will.jarvis.Jarvis;

public class CommandConsoleStop extends CommandConsole {
    private Jarvis jarvis;

    public CommandConsoleStop(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void execute(String... args) {
        jarvis.stop();
    }
}
