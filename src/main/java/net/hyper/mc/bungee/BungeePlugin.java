package net.hyper.mc.bungee;

import balbucio.responsivescheduler.RSTask;
import balbucio.responsivescheduler.ResponsiveScheduler;
import com.google.gson.Gson;
import net.hyper.mc.bungee.network.NetworkManager;
import net.hyper.mc.bungee.queue.QueueManager;
import net.hyper.mc.bungee.role.RoleManager;
import net.hyper.mc.bungee.server.Server;
import net.hyper.mc.msgbrokerapi.HyperMessageBroker;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BungeePlugin extends Plugin {

    private File file = new File("plugins/HyperSpigot/config.yml");
    private Configuration configuration;
    private ResponsiveScheduler scheduler;
    private HyperMessageBroker broker;
    private RoleManager roleManager;
    private NetworkManager networkManager;
    private QueueManager queueManager;

    public static List<ServerInfo> LOBBIES = new ArrayList<>();
    public static List<ServerInfo> WAIT_LOBBIES = new ArrayList<>();
    public static List<Server> SERVER_ITEMS = new ArrayList<>();
    private Gson gson = new Gson();
    @Override
    public void onEnable() {
        loadFile();
        scheduler = new ResponsiveScheduler();
        broker = new HyperMessageBroker(configuration.getString("hypermessagebroker.ip"), configuration.getInt("hypermessagebroker.port"), scheduler);
        roleManager = new RoleManager(this, broker);
        queueManager = new QueueManager(this, broker, configuration);
        networkManager = new NetworkManager(broker);
        scheduler.repeatTask(new RSTask() {
            @Override
            public void run() {
                roleManager.sendToSpigot();
                loadFile();
                JSONArray array = new JSONArray();
                for(Server server : SERVER_ITEMS){
                    array.put(gson.toJson(server));
                }
                broker.sendMessage("hyperspigot-serveritem", new JSONObject().put("itens", array));
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
            LOBBIES.clear();
            for(String s : configuration.getStringList("lobbies")){
                ServerInfo sv = ProxyServer.getInstance().getServerInfo(s);
                if(sv != null){
                    LOBBIES.add(sv);
                }
            }
            WAIT_LOBBIES.clear();
            for(String s : configuration.getStringList("wait-lobbies")){
                ServerInfo sv = ProxyServer.getInstance().getServerInfo(s);
                if(sv != null){
                    WAIT_LOBBIES.add(sv);
                }
            }
            File svFile = new File(folder, "servers.yml");
            if(!svFile.exists()){
                Files.copy(this.getClass().getResourceAsStream("/servers.yml"), svFile.toPath());
            }
            Configuration sv = YamlConfiguration.getProvider(YamlConfiguration.class).load(svFile);
            SERVER_ITEMS.clear();
            for(String s : sv.getSection("itens").getKeys()){
                Server server = new Server(
                        sv.getString("itens."+s+".name"),
                        sv.getInt("itens."+s+".slot"),
                        sv.getStringList("itens."+s+".description"),
                        sv.getStringList("itens."+s+".bungee-servers"),
                        sv.getString("itens."+s+".item"),
                        sv.getBoolean("itens."+s+".prime"),
                        sv.getBoolean("itens."+s+".connectable")
                );
                SERVER_ITEMS.add(server);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
    }

    public static ServerInfo getLobby(){
        return LOBBIES.stream().min(Comparator.comparingInt(serverInfo -> serverInfo.getPlayers().size())).get();
    }
    public static ServerInfo getWaitLobby(){
        return WAIT_LOBBIES.stream().min(Comparator.comparingInt(serverInfo -> serverInfo.getPlayers().size())).get();
    }
}
