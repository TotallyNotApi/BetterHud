package cz.apigames.betterhud.plugin_old.Events;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Configs.ConfigManager;
import cz.apigames.betterhud.plugin_old.Events.CustomEvents.PlayerEntersWaterEvent;
import cz.apigames.betterhud.plugin_old.Events.CustomEvents.PlayerLeavesWaterEvent;
import cz.apigames.betterhud.plugin_old.Events.CustomEvents.PlayerWaterChecker;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public class ToggleActions implements Listener {

    public static List<Hud> onJoin = new ArrayList<>();
    public static List<Hud> onSwim = new ArrayList<>();
    public static List<Hud> onDamageEntity = new ArrayList<>();
    public static List<Hud> onDamagePlayer = new ArrayList<>();
    public static List<Hud> onGMChange = new ArrayList<>();

    public static void assignActions(String hudName) {

        //JOIN
        if(ConfigManager.getConfig("config.yml").isSet("configuration.huds."+hudName+".toggle-actions.on-join")) {
            if(ConfigManager.getConfig("config.yml").getBoolean("configuration.huds."+hudName+".toggle-actions.on-join"))
                onJoin.add(Hud.getByName(hudName));
        }

        //UNDERWATER
        if(ConfigManager.getConfig("config.yml").isSet("configuration.huds."+hudName+".toggle-actions.underwater")) {
            if(ConfigManager.getConfig("config.yml").getBoolean("configuration.huds."+hudName+".toggle-actions.underwater"))
                if(onSwim.isEmpty()) {
                    PlayerWaterChecker.init(BetterHud.getPlugin(), 10);
                }
                onSwim.add(Hud.getByName(hudName));
        }

        //DAMAGE - entity
        if(ConfigManager.getConfig("config.yml").isSet("configuration.huds."+hudName+".toggle-actions.on-damage-by-entity")) {
            if(ConfigManager.getConfig("config.yml").getBoolean("configuration.huds."+hudName+".toggle-actions.on-damage-by-entity"))
                onDamageEntity.add(Hud.getByName(hudName));
        }

        //DAMAGE - player
        if(ConfigManager.getConfig("config.yml").isSet("configuration.huds."+hudName+".toggle-actions.on-damage-by-player")) {
            if(ConfigManager.getConfig("config.yml").getBoolean("configuration.huds."+hudName+".toggle-actions.on-damage-by-player"))
                onDamagePlayer.add(Hud.getByName(hudName));
        }

        //GAMEMODE
        if(ConfigManager.getConfig("config.yml").isSet("configuration.huds."+hudName+".toggle-actions.on-gamemode-change-to")) {

            try {
                GameMode.valueOf(ConfigManager.getConfig("config.yml").getString("configuration.huds."+hudName+".toggle-actions.on-gamemode-change-to"));
            } catch (IllegalArgumentException e) {
                BetterHud.error("&cInvalid gamemode in toggle action for hud &4"+hudName+"&c!", e);
                return;
            }

            onGMChange.add(Hud.getByName(hudName));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        if(!onJoin.isEmpty()) {
            for(Hud hud : onJoin) {

                if(hud.hasPermission()) {
                    if(player.hasPermission(hud.getPermission())) {
                        new Display(player, hud.getName());
                        return;
                    }
                } else {
                    new Display(player, hud.getName());
                    return;
                }

            }
        }

    }

    @EventHandler
    public void onPlayerUnderwaterEnter(PlayerEntersWaterEvent event) {

        Player player = event.getPlayer();

        if(!onSwim.isEmpty()) {

            for(Hud hud : onSwim) {

                if(hud.hasPermission()) {
                    if(player.hasPermission(hud.getPermission())) {

                        if(Display.isActive(player)) {
                            if(!Display.getByPlayer(player).getActiveHud().equals(hud)) {
                                Display.getByPlayer(player).saveLastHud();
                            }
                        }
                        new Display(player, hud.getName());

                        return;
                    }
                } else {

                    if(Display.isActive(player)) {
                        if(!Display.getByPlayer(player).getActiveHud().equals(hud)) {
                            Display.getByPlayer(player).saveLastHud();
                        }
                    }
                    new Display(player, hud.getName());

                    return;
                }

            }
        }

    }

    @EventHandler
    public void onPlayerUnderwaterLeave(PlayerLeavesWaterEvent event) {

        Player player = event.getPlayer();

        if(!onSwim.isEmpty()) {
            for(Hud hud : onSwim) {

                if(Display.getByPlayer(player) != null) {
                    Display display = Display.getByPlayer(player);
                    if(display.getActiveHud().equals(hud)) {
                        display.hide();
                        if(display.isLastHudSet()) {
                            display.returnLastHud();
                        }

                    }
                }

            }
        }

    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if(event.getEntity().getType().equals(EntityType.PLAYER)) {

            Player player = (Player) event.getEntity();

            if(event.getDamager().getType().equals(EntityType.PLAYER)) {

                if(!onDamagePlayer.isEmpty()) {

                    for(Hud hud : onDamagePlayer) {

                        if(hud.hasPermission()) {
                            if(player.hasPermission(hud.getPermission())) {

                                if(Display.isActive(player)) {
                                    Display.getByPlayer(player).saveLastHud();
                                    Display.getByPlayer(player).returnLastHudDelayed();
                                }
                                new Display(player, hud.getName());

                                return;
                            }
                        } else {

                            if(Display.isActive(player)) {
                                Display.getByPlayer(player).saveLastHud();
                                Display.getByPlayer(player).returnLastHudDelayed();
                            }
                            new Display(player, hud.getName());

                            return;
                        }

                    }
                }

            } else {

                if(!onDamageEntity.isEmpty()) {

                    for(Hud hud : onDamageEntity) {

                        if(hud.hasPermission()) {
                            if(player.hasPermission(hud.getPermission())) {

                                if(Display.isActive(player)) {
                                    Display.getByPlayer(player).saveLastHud();
                                }
                                new Display(player, hud.getName());

                                return;
                            }
                        } else {

                            if(Display.isActive(player)) {
                                Display.getByPlayer(player).saveLastHud();
                            }
                            new Display(player, hud.getName());

                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {

        Player player = event.getPlayer();

        if(!onGMChange.isEmpty()) {

            for(Hud hud : onGMChange) {
                GameMode mode;
                try {
                   mode  = GameMode.valueOf(ConfigManager.getConfig("config.yml").getString("configuration.huds."+hud.getName()+".toggle-actions.on-gamemode-change-to"));
                } catch (IllegalArgumentException e) {
                    BetterHud.error("&cInvalid gamemode in toggle action for hud &4"+hud.getName()+"&c!", e);
                    return;
                }

                if(mode.equals(event.getNewGameMode())) {

                    if(hud.hasPermission()) {
                        if(player.hasPermission(hud.getPermission())) {

                            if(Display.isActive(player)) {
                                Display.getByPlayer(player).saveLastHud();
                            }
                            new Display(player, hud.getName());

                            return;
                        }
                    } else {

                        if(Display.isActive(player)) {
                            Display.getByPlayer(player).saveLastHud();
                        }
                        new Display(player, hud.getName());

                        return;
                    }
                } else if(mode.equals(event.getPlayer().getGameMode())) {

                    Display.getByPlayer(player).hide();
                    if(Display.getByPlayer(player).isLastHudSet()) {
                        Display.getByPlayer(player).returnLastHud();
                    }

                }
            }
        }
    }

}
