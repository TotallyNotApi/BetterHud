package cz.apigames.betterhud.api.Utils;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.BetterHudAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Level;

public class ToggleCommand extends BukkitCommand {

    private static String toggle_on;
    private static String toggle_off;

    public ToggleCommand(String command) {
        super(command);
        this.description = "Custom toggle command for BetterHud";
        this.usageMessage = "/"+command;
        setAliases(Collections.emptyList());
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            BetterHudAPI.getLoadedHuds().forEach(hud -> {

                Optional<ToggleEvent> optEvent = hud.getEvents().stream().filter(toggleEvent -> toggleEvent.getEventType().equals(ToggleEvent.EventType.COMMAND) && toggleEvent.getOpt_value().equalsIgnoreCase(s)).findFirst();

                optEvent.ifPresent(toggleEvent -> {

                    if(!hud.isVisible(player)) {
                        hud.renderFor(player, toggleEvent.getDisplayType(), toggleEvent.getHideAfter(), true);
                        if(toggle_on != null && !toggle_off.equals("")) {
                            player.sendMessage(MessageUtils.colorize(toggle_on).replace("{hudName}", hud.getName()));
                        }
                    } else {
                        hud.hide(player);
                        if(toggle_off != null && !toggle_off.equals("")) {
                            player.sendMessage(MessageUtils.colorize(toggle_off).replace("{hudName}", hud.getName()));
                        }
                    }

                });
            });
        } else {
            commandSender.sendMessage("&cThis command is for players only!");
        }
        return true;
    }

    private static void registerCommand(String command) {
        command = command.replace("/", "");
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            if (commandMap.getCommand(command) == null)
                commandMap.register("betterhud", new ToggleCommand(command));
        } catch (NoSuchFieldException e) {
            Bukkit.getLogger().log(Level.SEVERE, "&cCant find commandMap inside Bukkit server class! Maybe outdated version of server? Please, contact plugin dev.");
        } catch (IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, "&cCant access commandMap inside Bukkit server class! Maybe outdated version of server? Please, contact plugin dev.");
        }
    }

    private static void unregisterCommand(String command) {
        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            if (commandMap.getCommand(command) != null)
                commandMap.getCommand(command).unregister(commandMap);
        } catch (NoSuchFieldException e) {
            Bukkit.getLogger().log(Level.SEVERE, "&cCant find commandMap inside Bukkit server class! Maybe outdated version of server? Please, contact plugin dev.");
        } catch (IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, "&cCant access commandMap inside Bukkit server class! Maybe outdated version of server? Please, contact plugin dev.");
        } catch (NullPointerException e) {
            Bukkit.getLogger().log(Level.SEVERE, "&cCan't unregister command, maybe there is no command/command map? Please, contact plugin dev.");
        }
    }

    public static void registerCommands() {
        BetterHudAPI.getLoadedHuds().forEach(hud -> hud.getEvents().forEach(toggleEvent -> {
            if(toggleEvent.getEventType().equals(ToggleEvent.EventType.COMMAND)) {
                registerCommand(toggleEvent.getOpt_value());
            }
        }));
    }

    public static void unregisterCommands() {
        BetterHudAPI.getLoadedHuds().forEach(hud -> hud.getEvents().forEach(toggleEvent -> {
            if(toggleEvent.getEventType().equals(ToggleEvent.EventType.COMMAND)) {
                unregisterCommand(toggleEvent.getOpt_value());
            }
        }));
    }

    public static void setEnableMessage(String message) {
        toggle_on = message;
    }

    public static void setDisableMessage(String message) {
        toggle_off = message;
    }

}
