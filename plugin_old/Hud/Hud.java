package cz.apigames.betterhud.plugin_old.Hud;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Configs.ConfigManager;
import cz.apigames.betterhud.plugin_old.Events.ToggleActions;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;

import java.util.*;

public class Hud {

    private static HashMap<String, Hud> huds = new HashMap<>();
    public static HashMap<Hud, String> toggleCommands = new HashMap<>();
    public List<HudPart> parts = new ArrayList<>();
    private final String name;
    private String permission;
    private Display.Type displayType;

    private String guiTitle;
    private int guiTitleOffset;

    public Hud(String name) {

        this.name = name;
        huds.put(name, this);

        //PERMISSION
        if(ConfigManager.getConfig("config.yml").isSet("configuration.huds."+name+".permission")) {
            permission = ConfigManager.getConfig("config.yml").getString("configuration.huds."+name+".permission");
        }

        //DISPLAY TYPE
        if(ConfigManager.getConfig("config.yml").isSet("configuration.huds."+name+".display")) {
            try {
                displayType = Display.Type.valueOf(ConfigManager.getConfig("config.yml").getString("configuration.huds."+name+".display"));
            } catch (IllegalArgumentException e) {
                BetterHud.error("&cAn error occurred while loading hud &4"+name+"&c! Invalid display type!", e);
                return;
            }
        } else {
            BetterHud.sendErrorToConsole("&cAn error occurred while loading hud &4"+name+"&c! Display type not set!");
            return;
        }

        //PLACEHOLDER / GUI
        if(displayType.equals(Display.Type.PLACEHOLDER) || displayType.equals(Display.Type.GUI)) {
            if(BetterHud.isPAPILoaded()) {

                if(displayType.equals(Display.Type.GUI)) {

                    //TITLE
                    if(ConfigManager.getConfig("config.yml").isSet("configuration.huds."+name+".title")) {
                        guiTitle = ConfigManager.getConfig("config.yml").getString("configuration.huds."+name+".title");
                    } else {
                        guiTitle = "";
                    }

                    guiTitleOffset = ConfigManager.getConfig("config.yml").getInt("configuration.huds."+name+".title-offset");

                }

                initialize();
            } else {
                BetterHud.sendErrorToConsole("&cAn error occurred while loading hud &4" + name + "&c! Display type is PLACEHOLDER/GUI, but PAPI is not loaded!");
            }
            return;
        }

        //TOGGLE COMMAND
        if(ConfigManager.getConfig("config.yml").isSet("configuration.huds."+name+".toggle-command")) {
            toggleCommands.put(this, ConfigManager.getConfig("config.yml").getString("configuration.huds."+name+".toggle-command").replace("/", ""));
        }

        initialize();

    }

    public static Hud getByName(String name) {
        return huds.get(name);
    }

    public void initialize() {

        if(ConfigManager.getConfig("config.yml").isConfigurationSection("configuration.hud-content."+name)) {
            for(String partName : ConfigManager.getConfig("config.yml").getConfigurationSection("configuration.hud-content."+name).getKeys(false)) {
                HudPart part = new HudPart(name, partName);
                if(part.isInitialized()) parts.add(part);
            }
        } else {
            BetterHud.sendErrorToConsole("&cAn error occurred while loading hud &4"+name+"! &cHud content can't be found!");
            return;
        }
        ToggleActions.assignActions(name);

    }

    public boolean hasPermission() {
        return permission != null;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return toggleCommands.get(this);
    }

    public String getPermission() {
        return permission;
    }

    public Display.Type getDisplayType() {
        return displayType;
    }

    public String getGuiTitle() {
        return guiTitle;
    }

    public int getGuiTitleOffset() {
        return guiTitleOffset;
    }

    public boolean canBeToggled() {
        return displayType.equals(Display.Type.ACTIONBAR) || displayType.equals(Display.Type.BOSSBAR);
    }

    public static Collection<Hud> getHuds() {
        return huds.values();
    }

    public static boolean exists(String hudName) {
        return getByName(hudName) != null;
    }

    public static void loadHuds() {

        huds.clear();

        if(ConfigManager.getConfig("config.yml").isSet("configuration.huds")) {

            for(String hudName : ConfigManager.getConfig("config.yml").getConfigurationSection("configuration.huds").getKeys(false)) {
                new Hud(hudName);
            }

        } else {
            BetterHud.sendErrorToConsole("Failed to load huds! Invalid configuration.");
        }



    }

}
