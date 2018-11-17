package sr.will.jarvis.exception;

import net.dv8tion.jda.core.entities.Guild;
import sr.will.jarvis.module.Module;

public class ModuleNotEnabledException extends RuntimeException {
    public ModuleNotEnabledException(Module module, Guild guild) {
        super("Module " + module.getDescription().getName() + " is not enabled on guild " + guild.getId());
    }
}
