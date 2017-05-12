package sr.will.jarvis.module.vex;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.vex.command.CommandVexStats;

import java.util.ArrayList;
import java.util.Arrays;

public class ModuleVex extends Module {
    private Jarvis jarvis;

    public ModuleVex(Jarvis jarvis) {
        this.jarvis = jarvis;

        jarvis.commandManager.registerCommand("vexstats", new CommandVexStats(this));
    }

    @Override
    public void finishStart() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void reload() {

    }

    @Override
    public String getName() {
        return "Vex";
    }

    @Override
    public String getHelpText() {
        return "Vex related things";
    }

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        return new ArrayList<>(Arrays.asList(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE
        ));
    }

    @Override
    public boolean isDefaultEnabled() {
        return false;
    }
}
