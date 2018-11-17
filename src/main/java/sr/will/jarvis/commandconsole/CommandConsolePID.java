package sr.will.jarvis.commandconsole;

import sr.will.jarvis.Jarvis;

import java.lang.management.ManagementFactory;

public class CommandConsolePID extends CommandConsole {
    private Jarvis jarvis;

    public CommandConsolePID(Jarvis jarvis) {
        this.jarvis = jarvis;
    }

    public void execute(String... args) {
        Jarvis.getLogger().info(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    }
}
