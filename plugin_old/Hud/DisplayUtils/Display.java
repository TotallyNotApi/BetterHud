package cz.apigames.betterhud.plugin_old.Hud.DisplayUtils;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Displays.ActionBar;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Displays.BossBar;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import cz.apigames.betterhud.plugin_old.Hud.HudPart;
import cz.apigames.betterhud.plugin_old.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Display {

    public enum Type {

        ACTIONBAR,
        BOSSBAR,
        PLACEHOLDER,
        GUI

    }

    private static HashMap<Player, Display> playerMap = new HashMap<>();
    private Player player;
    private String activeHudName;
    private String lastHudName;
    private boolean isShown;

    public Display(Player player, String hudName) {

        Display display;

        if(!playerMap.containsKey(player)) {
            this.player = player;
            playerMap.put(player, this);
            display = this;
        } else {
            display = getByPlayer(player);
            display.hide();
        }

        if(hudName.equalsIgnoreCase("auto")) {
            display.activeHudName = getAutoHud(player);
        } else {
            if(Hud.exists(hudName)) {
                display.activeHudName = hudName;
            } else {
                display.activeHudName = getAutoHud(player);
                BetterHud.sendErrorToConsole("&cUnknown hud &4"+hudName+"&c! Default hud was showed to the player &4"+player.getName());
            }
        }
        display.show();

    }

    public static Display getByPlayer(Player player) {
        return playerMap.get(player);
    }

    public Hud getActiveHud() {
        return Hud.getByName(activeHudName);
    }

    public static boolean isActive(Player player) {

        return getByPlayer(player) != null;

    }

    public static String getAutoHud(Player player) {

        if(BetterHud.isPluginSetup()) {
            for(Hud hud : Hud.getHuds()) {

                if(hud.hasPermission()) {
                    if(player.hasPermission(hud.getPermission())) {
                        return hud.getName();
                    }
                } else {
                    return hud.getName();
                }

            }
        }
        return null;
    }

    public static String getHudContent(Player player, Hud hud) {

        StringBuilder stringBuilder = new StringBuilder();

        for(HudPart part : hud.parts) {
            String output = part.get(player);
            if(output != null) {
                stringBuilder.append(output);
            }
        }

        return MessageUtils.colorize(stringBuilder.toString());

    }

    public void show() {

        if(BetterHud.isPluginSetup()) {

            if(Hud.getByName(activeHudName) == null) {
                return;
            }

            Hud activeHud = Hud.getByName(activeHudName);

            //ACTIONBAR
            if(activeHud.getDisplayType().equals(Type.ACTIONBAR)) {
                new ActionBar(player, activeHud);
            }

            //BOSSBAR
            if(activeHud.getDisplayType().equals(Type.BOSSBAR)) {
                new BossBar(player, activeHud);
            }

            isShown = true;

        }

    }

    public void hide() {

        if(BetterHud.isPluginSetup()) {

            if(Hud.getByName(activeHudName) == null) {
                return;
            }

            Hud activeHud = Hud.getByName(activeHudName);

            //ACTIONBAR
            if(activeHud.getDisplayType().equals(Type.ACTIONBAR)) {
                if(ActionBar.getByPlayer(player) != null) {
                    ActionBar.getByPlayer(player).hide();
                }
            }

            //BOSSBAR
            if(activeHud.getDisplayType().equals(Type.BOSSBAR)) {
                if(BossBar.getByPlayer(player) != null) {
                    BossBar.getByPlayer(player).hide();
                }
            }

            isShown = false;

        }
    }

    public void remove() {

        playerMap.remove(player);
        if(ActionBar.getByPlayer(player) != null) {
            ActionBar.getByPlayer(player).hide();
        }
        if(BossBar.getByPlayer(player) != null) {
            BossBar.getByPlayer(player).hide();
        }

    }

    public void saveLastHud() {
        lastHudName = activeHudName;
        show();
    }

    public boolean isLastHudSet() {
        return Hud.getByName(lastHudName) != null;
    }

    public void returnLastHud() {
        activeHudName = lastHudName;
        show();
    }

    public void returnLastHudDelayed() {

        Bukkit.getScheduler().runTaskLater(BetterHud.getPlugin(), this::returnLastHud, 80);

    }

    public boolean isShown() {
        return isShown;
    }
}
