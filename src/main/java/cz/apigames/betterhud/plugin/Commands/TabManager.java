package cz.apigames.betterhud.plugin.Commands;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.Elements.Element;
import cz.apigames.betterhud.api.Hud;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class TabManager implements TabCompleter {

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        List<String> tabComplete = new ArrayList<>();

        if(args.length == 1) {
            List<String> commands = Arrays.asList("show", "hide", "setValue", "getValue", "resetValue", "showElement", "hideElement", "setX", "reload", "report-bug");

            StringUtil.copyPartialMatches(args[0], commands, tabComplete);
            Collections.sort(tabComplete);

            return tabComplete;
        }

        if(args.length == 2) {

            if(Arrays.asList("show", "hide", "setValue", "getValue", "resetValue", "showElement", "hideElement").contains(args[0])) {

                List<String> playerNames = new ArrayList<>();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }

                if(!args[0].equalsIgnoreCase("getValue")) {
                    playerNames.add("ALL");
                }

                StringUtil.copyPartialMatches(args[1], playerNames, tabComplete);
                Collections.sort(tabComplete);

                return tabComplete;

            } else if (args[0].equalsIgnoreCase("setX")) {

                List<String> hudNames = new ArrayList<>();
                for(Hud hud : BetterHud.getAPI().getLoadedHuds()) {
                    hudNames.add(hud.getName());
                }

                StringUtil.copyPartialMatches(args[1], hudNames, tabComplete);
                Collections.sort(tabComplete);

                if(tabComplete.isEmpty()) {
                    return Collections.singletonList("UNKNOWN_HUD");
                }

                return tabComplete;

            }

            return tabComplete;
        }

        if(args.length == 3) {

            if(Arrays.asList("show", "hide", "setValue", "getValue", "resetValue", "showElement", "hideElement").contains(args[0])) {

                List<String> hudNames = new ArrayList<>();
                for(Hud hud : BetterHud.getAPI().getLoadedHuds()) {
                    hudNames.add(hud.getName());
                }

                StringUtil.copyPartialMatches(args[2], hudNames, tabComplete);
                Collections.sort(tabComplete);

                if(tabComplete.isEmpty()) {
                    return Collections.singletonList("UNKNOWN_HUD");
                }

                return tabComplete;

            } else if(args[0].equalsIgnoreCase("setX")) {

                List<String> elementNames = new ArrayList<>();
                if(BetterHud.getAPI().hudExists(args[1])) {

                    for(Element element : BetterHud.getAPI().getHud(args[1]).getElements()) {
                        elementNames.add(element.getName());
                    }

                } else {
                    return Collections.singletonList("UNKNOWN_HUD");
                }

                StringUtil.copyPartialMatches(args[2], elementNames, tabComplete);
                Collections.sort(tabComplete);

                if(tabComplete.isEmpty()) {
                    return Collections.singletonList("UNKNOWN_ELEMENT");
                }

                return tabComplete;

            }

        }

        if(args.length == 4) {

            //SHOW
            if(args[0].equalsIgnoreCase("show")) {

                StringUtil.copyPartialMatches(args[3], Arrays.asList("BOSSBAR", "ACTIONBAR", "CHAT"), tabComplete);
                Collections.sort(tabComplete);

                return tabComplete;

            }

            else if(Arrays.asList("setValue", "getValue", "resetValue", "showElement", "hideElement").contains(args[0])) {

                List<String> elementNames = new ArrayList<>();
                if(BetterHud.getAPI().hudExists(args[2])) {

                    for(Element element : BetterHud.getAPI().getHud(args[2]).getElements()) {
                        elementNames.add(element.getName());
                    }

                } else {
                    return Collections.singletonList("UNKNOWN_HUD");
                }

                StringUtil.copyPartialMatches(args[3], elementNames, tabComplete);
                Collections.sort(tabComplete);

                if(tabComplete.isEmpty()) {
                    return Collections.singletonList("UNKNOWN_ELEMENT");
                }

                return tabComplete;

            } else if(args[0].equalsIgnoreCase("setX")) {

                if(!args[3].equals("")) {
                    if(!CommandManager.isNumber(args[3])) {
                        return Collections.singletonList("MUST_BE_NUMBER");
                    }
                }

            }

        }

        return tabComplete;
    }

}
