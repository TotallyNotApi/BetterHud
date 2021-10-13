package cz.apigames.betterhud.api.Utils.Listeners;

import cz.apigames.betterhud.api.BetterHudAPI;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;

class ItemsAdderLoad implements Listener {

    @EventHandler
    public void onLoad(ItemsAdderLoadDataEvent event) {

        if(BetterHudAPI.getFontImagesDirectory() != null) {

            if(BetterHudAPI.getFontImagesDirectory().isDirectory()) {
                for(File child : BetterHudAPI.getFontImagesDirectory().listFiles()) {

                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(child);

                    String namespace = yaml.getString("info.namespace");

                    BetterHudAPI.fontImageCharacters.clear();
                    for(String name : yaml.getConfigurationSection("font_images").getKeys(false)) {

                        BetterHudAPI.fontImageCharacters.put(name, new FontImageWrapper(namespace + ":" + name));

                    }

                }
            }
        }

    }

}
