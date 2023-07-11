package net.hyper.mc.bungee.network;

import net.hyper.mc.msgbrokerapi.HyperMessageBroker;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkManager {

    private HyperMessageBroker broker;
    private CopyOnWriteArrayList<String> onlinePlayersInNetwork = new CopyOnWriteArrayList<>();

    public NetworkManager(HyperMessageBroker broker){
        this.broker = broker;
        broker.registerConsumer("hyperspigot-network", m -> {
            JSONObject data = new JSONObject((String) m.getValue());
            String channel = data.getString("channel");
            if(channel.equalsIgnoreCase("newplayer")){
                onlinePlayersInNetwork.add(data.getString("name"));
            } else if(channel.equalsIgnoreCase("quitplayer")){
                onlinePlayersInNetwork.remove(data.getString("name"));
            }
        });
    }

    public boolean hasPlayer(String name) {
        return onlinePlayersInNetwork.contains(name);
    }

    public List<String> getOnlinePlayers() {
        return (List<String>) onlinePlayersInNetwork;
    }

    public int getOnlineCount() {
        return onlinePlayersInNetwork.size();
    }
}
