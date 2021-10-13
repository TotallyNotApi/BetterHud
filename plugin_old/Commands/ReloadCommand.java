package cz.apigames.betterhud.plugin_old.Commands;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Configs.ConfigManager;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.DisplayRunnable;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import cz.apigames.betterhud.plugin_old.Utils.FileUtils;
import cz.apigames.betterhud.plugin_old.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand {

    public static void reloadAll(CommandSender sender) {

        //HIDE HUD
        for(Player targetPlayer : Bukkit.getOnlinePlayers()) {
            if(Display.isActive(targetPlayer)) {
                Display.getByPlayer(targetPlayer).hide();
            }
        }

        //RELOAD CONFIGS
        ConfigManager.reloadConfig("config.yml");
        ConfigManager.reloadConfig("messages.yml");

        //DISPLAY INIT
        int period = ConfigManager.getConfig("config.yml").getInt("configuration.hud-refresh.period");
        DisplayRunnable.initialize(period);

        //VANILLA HUD
        if(BetterHud.isIASelfHosted() && ConfigManager.getConfig("config.yml").isSet("configuration.show-vanilla-hud")) {
            FileUtils.vanillaHud(ConfigManager.getConfig("config.yml").getBoolean("configuration.show-vanilla-hud"));
        }

        //RELOAD HUDS
        Hud.loadHuds();
        FileUtils.generateIPFile();

        BetterHud.reloadIA();

        ToggleCommand.registerCommands();

        if(BetterHud.ConsoleError) {
            sender.sendMessage(MessageUtils.getMessage("reload-error"));
        } else {
            sender.sendMessage(MessageUtils.getMessage("reload-successful"));
        }

        Bukkit.getScheduler().runTaskLater(BetterHud.getPlugin(), () -> {

            for(Player targetPlayer : Bukkit.getOnlinePlayers()) {
                if(Display.isActive(targetPlayer)) {
                    Display.getByPlayer(targetPlayer).show();
                }
            }

            if(BetterHud.isPAPILoaded()) {
                BetterHud.expansion.register();
            }

        },60);

    }

    public static void reloadConfigs(CommandSender sender) {

        //HIDE HUD
        for(Player targetPlayer : Bukkit.getOnlinePlayers()) {
            if(Display.isActive(targetPlayer)) {
                Display.getByPlayer(targetPlayer).hide();
            }
        }

        //RELOAD CONFIGS
        ConfigManager.reloadConfig("config.yml");
        ConfigManager.reloadConfig("messages.yml");

        //DISPLAY INIT
        int period = ConfigManager.getConfig("config.yml").getInt("configuration.hud-refresh.period");
        DisplayRunnable.initialize(period);

        //VANILLA HUD
        if(BetterHud.isIASelfHosted() && ConfigManager.getConfig("config.yml").isSet("configuration.show-vanilla-hud")) {
            FileUtils.vanillaHud(ConfigManager.getConfig("config.yml").getBoolean("configuration.show-vanilla-hud"));
        }

        //RELOAD HUDS
        Hud.loadHuds();
        FileUtils.generateIPFile();

        ToggleCommand.registerCommands();

        if(BetterHud.ConsoleError) {
            sender.sendMessage(MessageUtils.getMessage("reload-error"));
        } else {
            sender.sendMessage(MessageUtils.getMessage("reload-successful"));

            for(Player targetPlayer : Bukkit.getOnlinePlayers()) {
                if(Display.isActive(targetPlayer)) {
                    Display.getByPlayer(targetPlayer).show();
                }
            }

            if(BetterHud.isPAPILoaded()) {
                BetterHud.expansion.register();
            }
        }

    }

    public static void reloadIA(CommandSender sender) {

        //HIDE HUD
        for(Player targetPlayer : Bukkit.getOnlinePlayers()) {
            if(Display.isActive(targetPlayer)) {
                Display.getByPlayer(targetPlayer).hide();
            }
        }

        BetterHud.reloadIA();

        //SHOW HUD
        Bukkit.getScheduler().runTaskLater(BetterHud.getPlugin(), () -> {

            for(Player targetPlayer : Bukkit.getOnlinePlayers()) {
                if(Display.isActive(targetPlayer)) {
                    Display.getByPlayer(targetPlayer).show();
                }
            }

            if(BetterHud.isPAPILoaded()) {
                BetterHud.expansion.register();
            }

        },60);

        if(BetterHud.ConsoleError) {
            sender.sendMessage(MessageUtils.getMessage("reload-error"));
        } else {
            sender.sendMessage(MessageUtils.getMessage("reload-successful"));
        }
    }

}
