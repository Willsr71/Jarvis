package sr.will.jarvis.modules.minecraft.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.minecraft.ModuleMinecraft;

import java.awt.*;
import java.util.HashMap;

public class CommandMCStatus extends Command {
    private ModuleMinecraft module;

    public HashMap<String, String> statusEmotes = new HashMap<>();

    public CommandMCStatus(ModuleMinecraft module) {
        super("mcstatus", "mcstatus", "Show status of Minecraft services", module);
        this.module = module;

        statusEmotes.put("green", ":white_check_mark:");
        statusEmotes.put("yellow", ":caution:");
        statusEmotes.put("red", ":x:");
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        HashMap<String, String> status = module.getStatus();
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : status.keySet()) {
            stringBuilder.append(statusEmotes.get(status.get(key)));
            stringBuilder.append(' ');
            stringBuilder.append(key);
            stringBuilder.append('\n');
        }

        message.getChannel().sendMessage(new EmbedBuilder().setTitle("Minecraft Status").setColor(Color.GREEN).setDescription(stringBuilder.toString()).build()).queue();
    }
}
