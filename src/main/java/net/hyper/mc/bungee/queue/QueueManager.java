package net.hyper.mc.bungee.queue;

import net.hyper.mc.bungee.BungeePlugin;
import net.hyper.mc.msgbrokerapi.HyperMessageBroker;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

public class QueueManager {

    private Plugin plugin;
    private HyperMessageBroker broker;
    private Configuration config;
    private boolean forced = false;

    public QueueManager(Plugin plugin, HyperMessageBroker broker, Configuration config) {
        this.plugin = plugin;
        this.broker = broker;
        this.config = config;
        broker.registerConsumer("hyperlogin-send", m -> {
            String playername = (String) m.getValue();
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playername);
            if(player != null) {
                if (queueActived()) {
                    player.connect(BungeePlugin.getWaitLobby());
                } else{
                    player.connect(BungeePlugin.getLobby());
                }
            }
        });
    }

    public boolean queueActived(){
        boolean a = forced;
         if(ProxyServer.getInstance().getOnlineCount() > config.getInt("queue.activeCount")){
             a = true;
         }
        return a;
    }
}
