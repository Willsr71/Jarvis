package sr.will.jarvis.exception;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;

public class UserPermissionException extends RuntimeException {
    public UserPermissionException(Permission permission, Guild guild) {
        super("User does not have permission " + permission.name() + " in guild " + guild.getId());
    }
}
