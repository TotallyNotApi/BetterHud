package cz.apigames.betterhud.plugin_old.Commands;

import cz.apigames.betterhud.plugin_old.Hud.Hud;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class MainCommandCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabComplete = new ArrayList<>();

        if(args.length == 1) {
            List<String> commands = Arrays.asList("show", "hide", "editor", "reload", "help", "exportTextures", "report-bug");

            StringUtil.copyPartialMatches(args[0], commands, tabComplete);
            Collections.sort(tabComplete);

            return tabComplete;
        }
        if(args.length == 2) {

            if(args[0].equalsIgnoreCase("reload")) {

                List<String> commands = Arrays.asList("all", "config", "itemsadder");

                StringUtil.copyPartialMatches(args[1], commands, tabComplete);
                Collections.sort(tabComplete);

                return tabComplete;

            }

            if(args[0].equalsIgnoreCase("show")) {
                List<String> huds = new ArrayList<>();
                for(Hud hud : Hud.getHuds()) {
                    if(hud.canBeToggled()) {
                        huds.add(hud.getName());
                    }
                }

                StringUtil.copyPartialMatches(args[1], huds, tabComplete);
                Collections.sort(tabComplete);

                return tabComplete;
            }

            if(args[0].equalsIgnoreCase("hide")) {
                List<String> playerNames = new ArrayList<>();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                playerNames.add("ALL");

                StringUtil.copyPartialMatches(args[1], playerNames, tabComplete);
                Collections.sort(tabComplete);

                return tabComplete;
            }
        }
        if(args.length == 3) {

            if(args[0].equalsIgnoreCase("show")) {
                List<String> playerNames = new ArrayList<>();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
                playerNames.add("ALL");

                StringUtil.copyPartialMatches(args[2], playerNames, tabComplete);
                Collections.sort(tabComplete);

                return tabComplete;
            }

        }




        return tabComplete;
    }
}
