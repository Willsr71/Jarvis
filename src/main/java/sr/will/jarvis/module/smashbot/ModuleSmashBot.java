package sr.will.jarvis.module.smashbot;

import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.module.smashbot.command.CommandFlair;

import java.util.ArrayList;
import java.util.Arrays;

public class ModuleSmashBot extends Module {
    private Jarvis jarvis;

    public ModuleSmashBot(Jarvis jarvis) {
        super("SmashBot",
                "Things ported from SmashBot because this way it is easier to maintain",
                new ArrayList<>(Arrays.asList(
                        Permission.MESSAGE_READ,
                        Permission.MESSAGE_WRITE
                )),
                false);
        this.jarvis = jarvis;

        jarvis.commandManager.registerCommand("flair", new CommandFlair(this));
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
}
