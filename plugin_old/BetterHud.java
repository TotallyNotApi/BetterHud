package cz.apigames.betterhud.plugin_old;

import cz.apigames.betterhud.plugin_old.Commands.MainCommand;
import cz.apigames.betterhud.plugin_old.Commands.MainCommandCompleter;
import cz.apigames.betterhud.plugin_old.Commands.ToggleCommand;
import cz.apigames.betterhud.plugin_old.Configs.ConfigManager;
import cz.apigames.betterhud.plugin_old.Events.CustomEvents.PlayerWaterChecker;
import cz.apigames.betterhud.plugin_old.Events.EditorEvents;
import cz.apigames.betterhud.plugin_old.Events.PlayerJoin;
import cz.apigames.betterhud.plugin_old.Events.PlayerLeave;
import cz.apigames.betterhud.plugin_old.Events.ToggleActions;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.DisplayRunnable;
import cz.apigames.betterhud.plugin_old.Hud.Editor.EditMode;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import cz.apigames.betterhud.plugin_old.Utils.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class BetterHud extends JavaPlugin {

    private static cz.apigames.betterhud.BetterHud plugin;
    private static boolean HexSupport = false;
    private static boolean PAPI = false;
    public static boolean ConsoleError = false;
    public static PAPIExpansion expansion;

    public static final char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    public static final char[] special_chars = ",.-_!?/\\()[]@#:;+%=<>*".toCharArray();
    public static final char[] numbers = "0123456789".toCharArray();
    public static final char[] number_chars = ",.-".toCharArray();

    public static final String TEXTURE_LATEST_VERSION = "1.2";

    //TODO
    // DODELAT INTERNAL PLACEHOLDERY PRO AIR, ABSORPTION - MUZE BYT POMOCI DALSICH HUDU NEBO DVOU STAVU IKONY A TEXTU = PRAZDNY NEBO UKAZUJE HODNOTU

    @Override
    public void onEnable() {
        plugin = this;

        //CONFIG LOADER
        ConfigManager.loadConfig("config.yml");
        ConfigManager.loadConfig("messages.yml");

        //CONFIG UPDATER
        try {
            ConfigManager.updateConfigs();
        } catch (IOException e) {
            cz.apigames.betterhud.BetterHud.error("Failed to update plugin files! Check 'logs/errors.txt' for more info", e);
        }

        Logger.createNewLogs();

        debug("Debug file created");

        //HEX SUPPORT CHECK
        if(Bukkit.getServer().getVersion().contains("1.16")) {
            debug("Hex supported");
            HexSupport = true;
        }

        //DEPENDENCIES
        debug("Loading dependencies");
        loadDependencies();
        loadSoftDependencies();

        //HUD LOADER
        if(isPluginSetup()) {
            debug("Loading huds");
            Hud.loadHuds();
        }


        //COMMAND LOADER
        debug("Loading commands");
        getCommand("bh").setExecutor(new MainCommand());
        getCommand("betterhud").setExecutor(new MainCommand());
        getCommand("bh").setTabCompleter(new MainCommandCompleter());
        getCommand("betterhud").setTabCompleter(new MainCommandCompleter());

        //TOGGLE COMMAND LOADER
        debug("Loading toggle commands");
        if(isPluginSetup()) {
            ToggleCommand.registerCommands();
        }

        //REGISTER EVENTS
        debug("Registering events");
        Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLeave(), this);
        Bukkit.getPluginManager().registerEvents(new ToggleActions(), this);
        Bukkit.getPluginManager().registerEvents(new EditorEvents(), this);
        Bukkit.getPluginManager().registerEvents(new UpdateChecker(), this);

        //DISPLAY RUNNABLE
        debug("Initializing display runnable");
        int period = ConfigManager.getConfig("config.yml").getInt("configuration.hud-refresh.period");
        DisplayRunnable.initialize(period);

        //INIT SWIM EVENTS
        debug("Initializing custom events");
        PlayerWaterChecker.init(this, 10);

        //TEXTURE UPDATE
        if(isPluginSetup()) {
            debug("Checking for textures updates");
            FileUtils.updateTextures();
        }

        //VANILLA HUD
        if(isIASelfHosted() && ConfigManager.getConfig("config.yml").isSet("configuration.show-vanilla-hud")) {
            FileUtils.vanillaHud(ConfigManager.getConfig("config.yml").getBoolean("configuration.show-vanilla-hud"));
        }

        //PAPI EXPANSION REGISTER
        if(isPluginSetup() && PAPI) {
            debug("Registering PAPI Expansion");
            (expansion = new PAPIExpansion()).register();
        }

        //CHECK FOR UPDATE
        UpdateChecker.checkUpdate();

        debug("Plugin loaded");
        sendMessageToConsole("&aPlugin was successfully loaded! Version: &2"+getVersion());
    }

    @Override
    public void onDisable() {

        EditMode.exitAll();

        sendMessageToConsole("&cPlugin was successfully disabled! Version: &4"+getVersion());
    }

    private void loadDependencies() {

        if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            sendMessageToConsole("&aSuccessfully hooked into &2ItemsAdder&a!");
        } else {
            sendMessageToConsole("&cDependency &4ItemsAdder&c not found!");
            Bukkit.getPluginManager().disablePlugin(this);
        }

    }

    private void loadSoftDependencies() {

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            sendMessageToConsole("&aSuccessfully hooked into &2PlaceholderAPI&a!");
            PAPI = true;
        }
    }

    public static cz.apigames.betterhud.BetterHud getPlugin() {
        return plugin;
    }

    public static String getVersion() {
        return getPlugin().getDescription().getVersion();
    }

    public static boolean isHexSupported() {
        return HexSupport;
    }

    public static boolean isPAPILoaded() {
        return PAPI;
    }

    public static boolean isPluginSetup() {

        Path hudFile = Paths.get("plugins/ItemsAdder/data/items_packs/betterhud/hud_parts.yml");
        return Files.exists(hudFile);

    }

    public static void error(String errorMessage, Exception exception) {

        Bukkit.getConsoleSender().sendMessage(MessageUtils.colorize("&e[BetterHud] &c"+errorMessage));
        debug("Caught new error -> errors.txt");
        ConsoleError = true;

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {

            String timeStamp = "[" + new SimpleDateFormat("HH:mm:s").format(new Date()) + "] ";

            Logger.writeErrorMessage(timeStamp + "[v"+getVersion()+"] " + errorMessage+"\n \n");
            Logger.writeException(exception);
            Logger.writeErrorMessage("\n---------------------------------------------");

        });

    }

    public static void debug(String debugMessage) {

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {

            if(isDebugEnabled()) {
                String timeStamp = "[" + new SimpleDateFormat("HH:mm:s").format(new Date()) + "] ";
                Logger.writeDebugMessage(timeStamp + debugMessage);
            }

        });

    }

    public static void sendErrorToConsole(String errorMessage) {
        Bukkit.getConsoleSender().sendMessage(MessageUtils.colorize("&e[BetterHud] &c"+errorMessage));
        ConsoleError = true;
    }

    public static void sendMessageToConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(MessageUtils.colorize("&e[BetterHud] "+message));
    }

    public static boolean isIASelfHosted() {

        File IA_FILE = new File("plugins"+File.separator+"ItemsAdder", "config.yml");
        YamlConfiguration IA_CONFIG = YamlConfiguration.loadConfiguration(IA_FILE);
        return IA_CONFIG.getConfigurationSection("resource-pack").getConfigurationSection("self-host").getBoolean("enabled");

    }

    public static void reloadIA() {

        if(isIASelfHosted()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "iazip");
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "iareload");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "iatexture all");
        }

    }

    public static boolean isDebugEnabled() {
        return ConfigManager.getConfig("config.yml").getBoolean("configuration.debug.enabled");
    }
}

