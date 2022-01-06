package cz.apigames.betterhud.plugin.Utils;

import cz.apigames.betterhud.BetterHud;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class ConfigConverter {

    private static YamlConfiguration oldConfig;
    private static YamlConfiguration newConfig;

    public static void convert() throws IOException {

        oldConfig = ConfigManager.getConfig("config.yml");
        ConfigManager.deleteConfig("config.yml");

        if(!new File(BetterHud.getPlugin().getDataFolder(), "config.yml").exists()) {

            ConfigManager.loadConfig("config.yml");
            newConfig = ConfigManager.getConfig("config.yml");

            //CONFIGURATION
            convertBoolean("configuration.debug.enabled", "configuration.debug.enabled", false);
            convertBoolean("configuration.show-vanilla-hud", "configuration.show-vanilla-hud", false);

            //HUDS
            newConfig.set("huds", null);
            int refresh = (oldConfig.getInt("configuration.hud-refresh.period", 1))*1000;

            for(String hudName : oldConfig.getConfigurationSection("configuration.huds").getKeys(false)) {

                String display = oldConfig.getString("huds."+hudName+".display", "ACTIONBAR");

                newConfig.set("huds."+hudName+".refresh-interval", refresh); //REFRESH

                //PERMISSIONS
                if(oldConfig.isSet("configuration.huds."+hudName+".permission")) {

                    newConfig.set("huds."+hudName+".conditions.permission", "perm="+oldConfig.get("configuration.huds."+hudName+".permission"));

                }

                //TOGGLE COMMAND
                if(oldConfig.isSet(("configuration.huds."+hudName+".toggle-command"))) {

                    newConfig.set("huds."+hudName+".toggle-events.command.event", "COMMAND");
                    newConfig.set("huds."+hudName+".toggle-events.command.display", display);
                    newConfig.set("huds."+hudName+".toggle-events.command.value", oldConfig.getString(("configuration.huds."+hudName+".toggle-command")));

                }

                //TOGGLE ON JOIN
                if(oldConfig.getBoolean(("configuration.huds."+hudName+".toggle-actions.on-join"))) {

                    newConfig.set("huds."+hudName+".toggle-events.join.event", "PLAYER_JOIN");
                    newConfig.set("huds."+hudName+".toggle-events.join.display", display);
                    newConfig.set("huds."+hudName+".toggle-events.join.hide_after", 0);

                }

                //TOGGLE ON GAMEMODE CHANGE
                if(oldConfig.isSet(("configuration.huds."+hudName+".toggle-actions.on-gamemode-change-to"))) {

                    newConfig.set("huds."+hudName+".toggle-events.gamemode.event", "GAMEMODE_CHANGE");
                    newConfig.set("huds."+hudName+".toggle-events.gamemode.display", display);
                    newConfig.set("huds."+hudName+".toggle-events.gamemode.value", oldConfig.getString(("configuration.huds."+hudName+".toggle-actions.on-gamemode-change-to")));

                }

                //TOGGLE ON DAMAGE BY PLAYER
                if(oldConfig.getBoolean(("configuration.huds."+hudName+".toggle-actions.on-damage-by-player"))) {

                    newConfig.set("huds."+hudName+".toggle-events.gamemode.event", "DAMAGE_BY_PLAYER");
                    newConfig.set("huds."+hudName+".toggle-events.gamemode.display", display);

                }

                //TOGGLE ON DAMAGE BY ENTITY
                if(oldConfig.getBoolean(("configuration.huds."+hudName+".toggle-actions.on-damage-by-entity"))) {

                    newConfig.set("huds."+hudName+".toggle-events.gamemode.event", "DAMAGE_BY_ENTITY");
                    newConfig.set("huds."+hudName+".toggle-events.gamemode.display", display);

                }

                //TOGGLE ON UNDERWATER
                if(oldConfig.getBoolean(("configuration.huds."+hudName+".toggle-actions.underwater"))) {

                    newConfig.set("huds."+hudName+".conditions.air", "compare={oxygen}<300");

                }

                //CONTENT
                for(String element : oldConfig.getConfigurationSection("configuration.hud-content."+hudName).getKeys(false)) {

                    String type = oldConfig.getString("configuration.hud-content."+hudName+"."+element+".type", "NONE");
                    if(type.equalsIgnoreCase("ICON")) {
                        type = "IMAGE";
                        convertString("configuration.hud-content."+hudName+"."+element+".texture-path", "huds."+hudName+".elements."+element+".texture-path", "betterhud:images/example.png");
                    } else if(type.equalsIgnoreCase("NONE")) {
                        continue;
                    } else {
                        convertString("configuration.hud-content."+hudName+"."+element+".input", "huds."+hudName+".elements."+element+".value", "Example");
                    }

                    //POSITION X
                    int x = oldConfig.getInt("configuration.hud-content."+hudName+"."+element+".position-x");
                    newConfig.set("huds."+hudName+".elements."+element+".position-x", x+955);

                    //POSITION Y
                    int y = oldConfig.getInt("configuration.hud-content."+hudName+"."+element+".position-y");
                    newConfig.set("huds."+hudName+".elements."+element+".position-y", Math.min(0, y));

                    newConfig.set("huds."+hudName+".elements."+element+".type", type);

                    convertInteger("configuration.hud-content."+hudName+"."+element+".scale", "huds."+hudName+".elements."+element+".scale", 8);

                    //ALIGN
                    if(oldConfig.isSet("configuration.hud-content."+hudName+"."+element+".align")) {
                        convertString("configuration.hud-content."+hudName+"."+element+".align", "huds."+hudName+".elements."+element+".align", "left");
                    }

                }

                BetterHud.sendMessageToConsole("Hud &2"+hudName+" &ahas been converted successfully!");

            }

            newConfig.save(new File(BetterHud.getPlugin().getDataFolder(), "config.yml"));
            BetterHud.sendMessageToConsole("Configuration has been converted successfully!");

        } else {
            BetterHud.error("Failed to convert old config! Please, contact plugin dev.", new FileAlreadyExistsException("Failed to convert old config, because it still exists!"));
        }

        BetterHud.sendMessageToConsole("&2======================================");

    }

    private static void convertString(String oldPath, String newPath, String def) {
        newConfig.set(newPath, oldConfig.getString(oldPath, def));
    }

    private static void convertInteger(String oldPath, String newPath, Integer def) {
        newConfig.set(newPath, oldConfig.getInt(oldPath, def));
    }

    private static void convertBoolean(String oldPath, String newPath, boolean def) {
        newConfig.set(newPath, oldConfig.getBoolean(oldPath, def));
    }



}
