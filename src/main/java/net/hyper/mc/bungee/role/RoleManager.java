package net.hyper.mc.bungee.role;

import com.google.gson.Gson;
import net.hyper.mc.msgbrokerapi.HyperMessageBroker;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import net.hyper.mc.bungee.role.Role;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;

public class RoleManager {

    private File file = new File("plugins/HyperSpigot/roles.yml");
    private Configuration configuration;
    private Plugin plugin;
    private HyperMessageBroker broker;
    private ConcurrentHashMap<String, Role> roles = new ConcurrentHashMap<>();

    public RoleManager(Plugin plugin, HyperMessageBroker broker) {
        this.plugin = plugin;
        this.broker = broker;
        load();
        loadRoles();
    }

    private void load(){
        try {
            if (!file.exists()) {
                Files.copy(this.getClass().getResourceAsStream("/roles.yml"), file.toPath());
            }
            configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadRoles(){
        roles.clear();
        for(String key : configuration.getKeys()){
            Role role = new Role(
                    configuration.getString(key+".name"),
                    configuration.getString(key+".tag"),
                    configuration.getInt(key+".order"),
                    configuration.getInt(key+".partysize"),
                    configuration.getInt(key+".multiplier"),
                    configuration.getBoolean(key+".multicolor"),
                    configuration.getBoolean(key+".jumpqueue"),
                    configuration.getString(key+".permission"));
            roles.put(role.getName(), role);
        }
        sendToSpigot();
    }
    public void sendToSpigot(){
        Gson gson = new Gson();
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        roles.values().forEach(r -> array.put(gson.toJson(r)));
        json.put("roles", array);
        json.put("size", roles.size());
        broker.sendMessage("hyperspigot-roles", json.toString());
    }
}
