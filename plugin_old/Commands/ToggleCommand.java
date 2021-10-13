package cz.apigames.betterhud.plugin_old.Commands;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import cz.apigames.betterhud.plugin_old.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ToggleCommand extends BukkitCommand {

    public ToggleCommand(String name) {
        super(name);
        this.description = "Toggle command for hud: "+name;
        this.usageMessage = "/"+name;
        this.setAliases(new ArrayList<>());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, String[] args) {

        if(sender instanceof Player) {

            Player player = (Player) sender;
            for(Hud hud : Hud.toggleCommands.keySet()) {

                if(hud.getCommand().equalsIgnoreCase(this.getName())) {

                    toggle(hud, player);

                }
            }

        } else {
            sender.sendMessage(MessageUtils.getMessage("players-only"));
        }
        return true;
    }

    public static void registerCommand(String command) {

        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            if(commandMap.getCommand(command) == null) {
                commandMap.register(command, new ToggleCommand(command));
            }

        } catch (NoSuchFieldException e) {
            BetterHud.error("&cCant find commandMap inside Bukkit server class! Maybe outdated version of server? Please, contact plugin dev.", e);
        } catch (IllegalAccessException e) {
            BetterHud.error("&cCant access commandMap inside Bukkit server class! Maybe outdated version of server? Please, contact plugin dev.", e);
        }

    }

    public static void registerCommands() {

        for(Hud hud : Hud.toggleCommands.keySet()) {

            registerCommand(hud.getCommand());

        }

    }

    private static void toggle(Hud hud, Player player) {

        if(Display.getByPlayer(player) != null) {
            if(Display.getByPlayer(player).isShown()) {

                if(Display.getByPlayer(player).getActiveHud().equals(hud)) {

                    //HIDE
                    hide(Display.getByPlayer(player), player);

                } else {

                    //SHOW
                    show(hud, player);
                }

            } else {

                //SHOW
                show(hud, player);
            }
        } else {

            //SHOW
            new Display(player, hud.getName());
        }

    }

    private static void show(Hud hud, Player player) {

        if(hud.hasPermission()) {
            if(player.hasPermission(hud.getPermission())) {
                new Display(player, hud.getName());
                player.sendMessage(MessageUtils.getMessage("toggle-show").replace("{hudName}", hud.getName()));
            } else {
                player.sendMessage(MessageUtils.getMessage("no-permission"));
            }
        } else {
            new Display(player, hud.getName());
            player.sendMessage(MessageUtils.getMessage("toggle-show").replace("{hudName}", hud.getName()));
        }
    }

    private  static void hide(Display display, Player player) {
        player.sendMessage(MessageUtils.getMessage("toggle-hide").replace("{hudName}", display.getActiveHud().getName()));
        display.hide();
    }

}
