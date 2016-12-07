package sr.will.jarvis.command;

import net.dv8tion.jda.core.entities.Message;

public abstract class Command {
    public abstract void execute(Message message, String... args);
}
