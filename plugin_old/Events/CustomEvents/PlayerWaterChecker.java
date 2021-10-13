package cz.apigames.betterhud.plugin_old.Events.CustomEvents;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class PlayerWaterChecker implements Runnable, Listener {
    private final Set<Player> divingPlayers;

    private final Set<Player> walkingPlayers;

    private Set<Player> divingCopy;

    private Set<Player> walkingCopy;

    private final PluginManager pluginManager;

    public static void init(JavaPlugin plugin, long checkInterval) {
        PlayerWaterChecker instance = new PlayerWaterChecker();
        Bukkit.getScheduler().runTaskTimer(plugin, instance, 0L, checkInterval);
        Bukkit.getPluginManager().registerEvents(instance, plugin);
    }

    public PlayerWaterChecker() {
        this.divingPlayers = Sets.newHashSet();
        this.walkingPlayers = Sets.newHashSet();
        this.pluginManager = Bukkit.getPluginManager();
        Player[] players = (Player[])Bukkit.getOnlinePlayers().toArray((Object[])new Player[0]);
        for (Player target : players) {
            if (isPlayerInWater(target)) {
                this.divingPlayers.add(target);
            } else {
                this.walkingPlayers.add(target);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isPlayerInWater(player)) {
            this.divingPlayers.add(player);
        } else {
            this.walkingPlayers.add(player);
        }
    }

    public void run() {
        this.divingCopy = Sets.newHashSet(this.divingPlayers);
        this.walkingCopy = Sets.newHashSet(this.walkingPlayers);
        for (Player diving : this.divingCopy) {
            if (!isPlayerInWater(diving)) {
                this.divingPlayers.remove(diving);
                this.walkingPlayers.add(diving);
                this.pluginManager.callEvent(new PlayerLeavesWaterEvent(diving));
            }
        }
        for (Player walking : this.walkingCopy) {
            if (isPlayerInWater(walking)) {
                this.walkingPlayers.remove(walking);
                this.divingPlayers.add(walking);
                this.pluginManager.callEvent(new PlayerEntersWaterEvent(walking));
            }
        }
    }

    private boolean isPlayerInWater(Player player) {
        return player.getEyeLocation().getBlock().isLiquid();
    }
}
