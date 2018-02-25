package sr.will.jarvis.modules.minecraft;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.Permission;
import sr.will.jarvis.module.Module;
import sr.will.jarvis.modules.minecraft.command.CommandMCProfile;
import sr.will.jarvis.modules.minecraft.command.CommandMCStatus;

import java.util.HashMap;

public class ModuleMinecraft extends Module {
    public static final String statusApi = "https://status.mojang.com";
    private Gson gson = new Gson();

    public void initialize() {
        setNeededPermissions(
                Permission.MESSAGE_READ,
                Permission.MESSAGE_WRITE
        );
        setDefaultEnabled(false);

        registerCommand("mcprofile", new CommandMCProfile(this));
        registerCommand("mcstatus", new CommandMCStatus(this));
    }

    public void finishStart() {

    }

    public void stop() {

    }

    public void reload() {

    }

    public String getResponse(String url) {
        try {
            return Unirest.get(url).asString().getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return "";
    }

    public HashMap<String, String> getStatus() {
        HashMap[] terribleMaps = gson.fromJson(getResponse(statusApi + "/check"), HashMap[].class);
        HashMap<String, String> map = new HashMap<>();

        for (HashMap terribleMap : terribleMaps) {
            terribleMap.keySet().forEach(key -> map.put(key.toString(), terribleMap.get(key).toString())
            );
        }
        return map;
    }
}
