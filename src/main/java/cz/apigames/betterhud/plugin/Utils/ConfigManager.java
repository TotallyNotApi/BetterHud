package cz.apigames.betterhud.plugin.Utils;

import cz.apigames.betterhud.BetterHud;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ConfigManager {

    private static BetterHud plugin = BetterHud.getPlugin();
    private static HashMap<String, YamlConfiguration> configs = new HashMap<String, YamlConfiguration>() {
    };

    public static void loadConfig(String configName) {
        File ConfigFile = new File(plugin.getDataFolder(), configName);
        if (!ConfigFile.exists()) {
            plugin.saveResource(configName, false);
            BetterHud.sendMessageToConsole("&aFile &2" + configName + " &ahas been created!");
            configs.put(configName, YamlConfiguration.loadConfiguration(ConfigFile));
            return;
        }

        configs.put(configName, YamlConfiguration.loadConfiguration(ConfigFile));

    }

    public static void loadConfig(String configName, File file) {
        if(new File(plugin.getDataFolder(), configName).exists()) {
            configs.put(configName, YamlConfiguration.loadConfiguration(file));
        }
    }

    public static void unloadConfig(String configName) {
        configs.remove(configName);
    }

    public static boolean deleteConfig(String configName) {
        File file = new File(plugin.getDataFolder(), configName);
        if(file.exists()) {
            if(file.delete()) {
                unloadConfig(configName);
                return true;
            }
        }
        return false;
    }

    public static void reloadConfig(String configName) {
        loadConfig(configName);
    }

    public static YamlConfiguration getConfig(String configName) {

        try {
            return configs.get(configName);
        } catch (NullPointerException e) {
            BetterHud.error("Failed to get config file! File not found. Please, restart the server.", e);
        }
        return null;
    }

    public static void set(String configName, String path, Object data) {

        File configFile = new File(BetterHud.getPlugin().getDataFolder(), configName);
        YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(configFile);

        yamlFile.set(path, data);

        try {
            yamlFile.save(configFile);
        } catch (IOException e) {
            BetterHud.error("&cAn error occurred while writing data to "+configName+" (Path: "+path+")", e);
        }

    }

    private static Path createTempFile(String fileName) throws IOException {

        Path tempFile = Paths.get("plugins/BetterHud/tmp-"+fileName);
        Files.createFile(tempFile);

        InputStream inputStream = BetterHud.getPlugin().getResource(fileName);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);

        OutputStream outStream = new FileOutputStream(tempFile.toFile());
        outStream.write(buffer);
        inputStream.close();
        outStream.close();

        return tempFile;

    }

    public static void updateConfigs() throws IOException {

        Path tempConfig = createTempFile("config.yml");
        Path tempMessages = createTempFile("messages.yml");

        YamlConfiguration configYaml = YamlConfiguration.loadConfiguration(tempConfig.toFile());
        YamlConfiguration messagesYaml = YamlConfiguration.loadConfiguration(tempMessages.toFile());

        String currentVersion = getConfig("config.yml").getString("config-version");


        //FORCE CONVERSION FOR 1.1 CONFIGS
        //
        //FROM 1.1 TO 1.2
        if(currentVersion.equalsIgnoreCase("1.1")) {
            BetterHud.sendMessageToConsole("&2======================================");
            BetterHud.sendMessageToConsole("&aOld version of config detected!");

            //MESSAGES CONFIG DELETION
            if(deleteConfig("messages.yml")) {
                BetterHud.sendMessageToConsole("&aSuccessfully deleted old version of messages.yml!");
            } else {
                BetterHud.sendMessageToConsole("&cFailed to delete the old version of messages.yml.. Please, do it manually and reload the plugin.");
            }

            BetterHud.sendMessageToConsole("&aStarting conversion...");
            ConfigConverter.convert();
            loadConfig("messages.yml");
        }

        //DEFAULT
        else if(!configYaml.getString("config-version").equalsIgnoreCase(currentVersion)) {

            BetterHud.sendMessageToConsole("&aNew version of config files found! Updating..");

            //CONFIG
            for(String path : configYaml.getKeys(true)) {

                if(!path.contains("huds") && !path.contains("hud-content")) {

                    if(!getConfig("config.yml").isSet(path)) {
                        set("config.yml", path, configYaml.get(path));
                    }
                }

            }

            //MESSAGES
            for(String path : messagesYaml.getKeys(true)) {

                if(!getConfig("messages.yml").isSet(path)) {
                    set("messages.yml", path, messagesYaml.get(path));
                }

            }

            set("config.yml", "config-version", configYaml.get("config-version"));
            BetterHud.sendMessageToConsole("&aAll plugin files have been updated!");

        }

        Files.deleteIfExists(tempConfig);
        Files.deleteIfExists(tempMessages);

    }

}
