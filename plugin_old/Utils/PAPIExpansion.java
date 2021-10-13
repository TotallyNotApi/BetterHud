package cz.apigames.betterhud.plugin_old.Utils;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Displays.GUI;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Displays.Placeholder;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIExpansion extends PlaceholderExpansion {

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return "ApiGames";
    }

    @Override
    public String getRequiredPlugin(){
        return "BetterHud";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "betterhud";
    }

    @Override
    public @NotNull String getVersion() {
        return BetterHud.getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier){

        if(player == null){
            return "";
        }

        if(Hud.getByName(identifier) != null) {

            Hud hud = Hud.getByName(identifier);

            if(hud != null) {
                if(hud.hasPermission()) {
                    if(!player.hasPermission(hud.getPermission())) {
                        return "&r";
                    }
                }

                if(hud.getDisplayType().equals(Display.Type.PLACEHOLDER)) {
                    return Placeholder.get(player, hud);
                }
                else if(hud.getDisplayType().equals(Display.Type.GUI)) {
                    return GUI.get(player, hud);
                } else {
                    return Display.getHudContent(player, hud);
                }
            }

        }

        return "&r";
    }

}
