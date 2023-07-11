package net.hyper.mc.bungee;

import balbucio.responsivescheduler.RSTask;
import balbucio.responsivescheduler.ResponsiveScheduler;
import net.hyper.mc.bungee.network.NetworkManager;
import net.hyper.mc.bungee.role.RoleManager;
import net.hyper.mc.msgbrokerapi.HyperMessageBroker;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;

public final class BungeePlugin extends Plugin {

    private File file = new File("plugins/HyperSpigot/config.yml");
    private Configuration configuration;
    private ResponsiveScheduler scheduler;
    private HyperMessageBroker broker;
    private RoleManager roleManager;
    private NetworkManager networkManager;

    @Override
    public void onEnable() {
        loadFile();
        scheduler = new ResponsiveScheduler();
        broker = new HyperMessageBroker(configuration.getString("hypermessagebroker.ip"), configuration.getInt("hypermessagebroker.port"), scheduler);
        roleManager = new RoleManager(this, broker);
        scheduler.repeatTask(new RSTask() {
            @Override
            public void run() {
                roleManager.sendToSpigot();
            }
        }, 100, 10000);
    }

    private void loadFile(){
        try {
            File folder = new File("plugins/HyperSpigot");
            folder.mkdir();
            if (!file.exists()) {
                Files.copy(this.getClass().getResourceAsStream("/config.yml"), file.toPath());
            }
            configuration = YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
    }
}
