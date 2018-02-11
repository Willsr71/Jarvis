package sr.will.jarvis.modules.assistance.command;

import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.Jarvis;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.assistance.ModuleAssistance;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.StringWriter;

public class CommandEval extends Command {
    private ModuleAssistance module;

    public CommandEval(ModuleAssistance module) {
        super("eval", "eval <stuff>", "Eval stuff", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        // Only allow the bot owner to restart the bot
        if (!Jarvis.getInstance().config.discord.owners.contains(message.getAuthor().getId())) {
            sendFailureMessage(message, "You don't have permission for that");
            return;
        }

        String script = message.getContentDisplay().replace("!eval ", "");

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine factory = manager.getEngineByName("javascript");
        ScriptContext context = factory.getContext();
        StringWriter writer = new StringWriter();
        context.setWriter(writer);

        try {
            factory.eval(script);

            sendSuccessMessage(message, writer.toString(), false);
        } catch (ScriptException e) {
            sendFailureMessage(message, e.getMessage());
        }
    }
}
