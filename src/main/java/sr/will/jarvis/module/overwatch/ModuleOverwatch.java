package sr.will.jarvis.module.overwatch;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.overwatch.command.CommandOWStats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuleOverwatch extends Module {
    private Jarvis jarvis;

    public static String BATTLETAG_REGEX = "(?i).{3,12}\\#[0-9]{4,5}";

    public ModuleOverwatch(Jarvis jarvis) {
        this.jarvis = jarvis;

        jarvis.commandManager.registerCommand("owstats", new CommandOWStats(this));
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
        return "Overwatch";
    }

    @Override
    public String getHelpText() {
        return "Overwatch related commands";
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

    public boolean isValidBattleTag(String battletag) {
        Matcher matcher = Pattern.compile(BATTLETAG_REGEX).matcher(battletag);
        return matcher.find();
    }
}
