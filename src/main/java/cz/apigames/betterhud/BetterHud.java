package cz.apigames.betterhud;

import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Utils.MessageUtils;
import cz.apigames.betterhud.plugin.Commands.CommandManager;
import cz.apigames.betterhud.plugin.Commands.TabManager;
import cz.apigames.betterhud.plugin.Utils.*;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class BetterHud extends JavaPlugin {

    private static BetterHud plugin;
    private static BetterHudAPI api;
    public static Set<String> checksums = new HashSet<>();

    @Override
    public void onEnable() {
        plugin = this;

        if(loadDependencies()) {

            //CONFIG LOADER
            ConfigManager.loadConfig("config.yml");
            ConfigManager.loadConfig("characters.yml");
            ConfigManager.loadConfig("messages.yml");

            //CONFIG UPDATER
            try {
                ConfigManager.updateConfigs();
            } catch (IOException | NullPointerException e) {
                error("Failed to update plugin files! Check 'logs/errors.txt' for more info", e);
            }

            //LOGGER
            Logger.createNewLogs();

            //UPDATE CHECKER
            UpdateChecker.checkUpdate();

            //EXTRACT TEXTURES
            if(!new File("plugins/ItemsAdder/data/items_packs/betterhud").exists()) {

                try {
                    if(TextureExtractor.extract()) {
                        sendMessageToConsole("Default textures have been exported successfully!");
                    } else {
                        sendErrorToConsole("Failed to export default textures from JAR file!");
                    }
                } catch (IOException e) {
                    error("Failed to export default textures from JAR file!", e);
                }

            }

            // --- BetterHudAPI --- \\

            BetterHudAPI.registerExceptionListener(new Exceptions());

            api = new BetterHudAPI(this);
            List<String> errors = api.load(new File(this.getDataFolder(), "config.yml"), true);
            Future<Boolean> future =  api.generateFontImageFiles(new File(this.getDataFolder(), "characters.yml"), new File("plugins/ItemsAdder/data/items_packs/betterhud"));

            //CHECKSUM INIT
            try {
                if(future.get(5, TimeUnit.SECONDS)) {
                    for(File child : BetterHudAPI.getFontImagesDirectory().listFiles()) {
                        checksums.add(FileUtils.checksum(child));

                        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(child);

                        String namespace = yaml.getString("info.namespace");

                        BetterHudAPI.fontImageCharacters.clear();

                        Optional<String> optName = yaml.getConfigurationSection("font_images").getKeys(false).stream().findFirst();
                        if(optName.isPresent()) {
                            if(new FontImageWrapper(namespace + ":" + optName.get()).exists()) {
                                for(String name : yaml.getConfigurationSection("font_images").getKeys(false)) {
                                    BetterHudAPI.fontImageCharacters.put(name, new FontImageWrapper(namespace + ":" + name));
                                }
                            }
                        }

                    }
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                error("Failed to initialize FontImage checksum set!", e);
            }



            //ERROR MESSAGES
            if(!errors.isEmpty()) {
                sendErrorToConsole("========================================");
                sendErrorToConsole("BetterHud - Found configuration errors");
                sendErrorToConsole(" ");
                errors.forEach(BetterHud::sendErrorToConsole);
                sendErrorToConsole(" ");
                sendErrorToConsole("========================================");
            }

            //COMMANDS
            getCommand("betterhud").setExecutor(new CommandManager());
            getCommand("betterhud").setTabCompleter(new TabManager());
            getCommand("bh").setExecutor(new CommandManager());
            getCommand("bh").setTabCompleter(new TabManager());
        }

        sendMessageToConsole("&aPlugin was successfully loaded! Version: &2"+getVersion());
    }

    @Override
    public void onDisable() {

        api.unload();

        sendMessageToConsole("&cPlugin was successfully disabled! Version: &4"+getVersion());
    }

    private boolean loadDependencies() {

        if(Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            sendMessageToConsole("&aSuccessfully hooked into &2ItemsAdder&a!");
            return true;
        } else {
            sendMessageToConsole("&cDependency &4ItemsAdder&c not found!");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        return false;
    }

    public static BetterHud getPlugin() {
        return plugin;
    }

    public static String getVersion() {
        return getPlugin().getDescription().getVersion();
    }

    public static void sendErrorToConsole(String errorMessage) {
        Bukkit.getConsoleSender().sendMessage(MessageUtils.colorize("&e[BetterHud] &c"+errorMessage));
    }

    public static void sendMessageToConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(MessageUtils.colorize("&e[BetterHud] &a"+message));
    }

    public static boolean isIASelfHosted() {

        File IA_FILE = new File("plugins/ItemsAdder", "config.yml");
        YamlConfiguration IA_CONFIG = YamlConfiguration.loadConfiguration(IA_FILE);
        return IA_CONFIG.getConfigurationSection("resource-pack").getConfigurationSection("self-host").getBoolean("enabled");

    }

    public static boolean isDebugEnabled() {
        return ConfigManager.getConfig("config.yml").getBoolean("configuration.debug.enabled");
    }

    public static void error(String errorMessage, Exception exception) {

        Bukkit.getConsoleSender().sendMessage(MessageUtils.colorize("&e[BetterHud] &c"+errorMessage));
        debug("Caught new error -> errors.txt");

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

    public static BetterHudAPI getAPI() {
        return api;
    }

    public static String getMessage(String path) {

        if(ConfigManager.getConfig("messages.yml").isSet("messages."+path)) {
            return MessageUtils.colorize(ConfigManager.getConfig("messages.yml").getString("messages."+path));
        } else {
            return MessageUtils.colorize(" &eBetterHud &8» &cUnknown message &4'" + path + "'&c, check your messages.yml");
        }

    }

}
