package cz.apigames.betterhud.api.Utils.Listeners;

import cz.apigames.betterhud.api.BetterHudAPI;
import org.bukkit.Bukkit;

public class ListenerRegister {

    public static void registerListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new ItemsAdderLoad(), BetterHudAPI.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoin(), BetterHudAPI.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerLeave(), BetterHudAPI.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new GamemodeChange(), BetterHudAPI.getPlugin());
    }

}
