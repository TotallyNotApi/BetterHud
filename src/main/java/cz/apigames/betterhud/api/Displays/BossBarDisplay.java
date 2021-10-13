package cz.apigames.betterhud.api.Displays;

import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Hud;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.Objects;

public class BossBarDisplay extends Display {

    protected NamespacedKey key;

    public BossBarDisplay(Player player, Hud hud) {
        super(player, hud);

        this.key = new NamespacedKey(BetterHudAPI.getPlugin(), "hud_"+hud.getName()+"-"+player.getName());
        Bukkit.createBossBar(key, "", BarColor.YELLOW, BarStyle.SOLID);
        Objects.requireNonNull(Bukkit.getBossBar(key)).setProgress(0.0);
        Objects.requireNonNull(Bukkit.getBossBar(key)).addPlayer(player);

    }

    @Override
    public void init() {

        super.taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(BetterHudAPI.getPlugin(), this::render, 0, (hud.getRefreshInterval()/1000)*20).getTaskId();

    }

    @Override
    public void render() {

        if(key != null && player != null) {
            Objects.requireNonNull(Bukkit.getBossBar(key)).setTitle(hud.getRenderedString(player));
        }

    }

    @Override
    public void destroy() {

        if(key != null) {
            Objects.requireNonNull(Bukkit.getBossBar(key)).removePlayer(player);
            Bukkit.removeBossBar(key);
        }

        key = null;
        super.hud = null;
        super.player = null;
        Bukkit.getScheduler().cancelTask(super.taskID);

        displays.remove(this);

    }
}
