package cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Displays;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

public class BossBar {

    private static HashMap<Player, BossBar> playerMap = new HashMap<>();
    private Player player;
    private Hud displayHud;
    private NamespacedKey bossbarKey;

    public BossBar(Player player, Hud displayHud) {

        if(!playerMap.containsKey(player)) {
            this.player = player;
            this.displayHud = displayHud;
            playerMap.put(player, this);
            createBossBar();
        } else {
            getByPlayer(player).displayHud = displayHud;
        }

    }

    public static BossBar getByPlayer(Player player) {
        return playerMap.get(player);
    }

    public void hide() {
        if(bossbarKey != null) {
            Objects.requireNonNull(Bukkit.getBossBar(bossbarKey)).removePlayer(player);
            Bukkit.removeBossBar(bossbarKey);
            playerMap.remove(player);
        }
    }

    public static Collection<BossBar> getAll() {
        return playerMap.values();
    }

    public void update() {

        if(Bukkit.getBossBar(bossbarKey) == null) {
            playerMap.remove(player);
            return;
        }

        //RUN SYNC
        Bukkit.getScheduler().runTask(BetterHud.getPlugin(), () -> Objects.requireNonNull(Bukkit.getBossBar(bossbarKey)).setTitle(Display.getHudContent(player, displayHud)));
    }

    //PRIVATE PART

    private void createBossBar() {
        this.bossbarKey = new NamespacedKey(BetterHud.getPlugin(), "betterhud-"+player.getName());
        Bukkit.createBossBar(bossbarKey, "", BarColor.YELLOW, BarStyle.SOLID);
        Objects.requireNonNull(Bukkit.getBossBar(bossbarKey)).setProgress(0.0);
        Objects.requireNonNull(Bukkit.getBossBar(bossbarKey)).addPlayer(player);
    }

/*    public void setMessage(String message) {
        Objects.requireNonNull(Bukkit.getBossBar(bossbarKey)).setTitle("");
    }

    public void setFill(double percent) {

        if(percent > 1.0) {
            percent = 1.0;
        }
        if(percent < 0) {
            percent = 0;
        }

        Objects.requireNonNull(Bukkit.getBossBar(bossbarKey)).setProgress(percent);

    } */


}
