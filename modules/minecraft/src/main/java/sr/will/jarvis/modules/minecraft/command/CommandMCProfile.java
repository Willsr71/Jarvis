package sr.will.jarvis.modules.minecraft.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.noxal.common.util.UUIDFetcher;
import sr.will.jarvis.command.Command;
import sr.will.jarvis.modules.minecraft.ModuleMinecraft;

import java.awt.*;
import java.util.UUID;

public class CommandMCProfile extends Command {
    private ModuleMinecraft module;

    public CommandMCProfile(ModuleMinecraft module) {
        super("mcprofile", "mcprofile <username | UUIDResponse>", "Display info about a user", module);
        this.module = module;
    }

    @Override
    public void execute(Message message, String... args) {
        checkModuleEnabled(message, module);

        if (args.length != 1) {
            sendUsage(message);
            return;
        }

        String username = args[0];
        UUID uuid;
        try {
            uuid = UUIDFetcher.getUUIDOf(username);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (uuid == null) {
            sendFailureMessage(message, "No user found");
            return;
        }

        EmbedBuilder embed = new EmbedBuilder().setTitle("Minecraft Profile").setColor(Color.GREEN);
        embed.addField("Username", username, false);
        embed.addField("UUID", uuid.toString(), false);
        embed.setThumbnail("https://crafatar.com/renders/body/" + uuid.toString().replaceAll("-", ""));

        message.getChannel().sendMessage(embed.build()).queue();
    }
}
