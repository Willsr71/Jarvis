package sr.will.jarvis.exception;


import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

public class BotPermissionException extends RuntimeException {
    public BotPermissionException(Permission permission, Guild guild) {
        super("Bot does not have permission " + permission.name() + " in guild " + guild.getId());
    }
}
