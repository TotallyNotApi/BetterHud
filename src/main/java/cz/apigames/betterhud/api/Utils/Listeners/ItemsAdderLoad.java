package cz.apigames.betterhud.api.Utils.Listeners;

import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Elements.ImageElement;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.Objects;

class ItemsAdderLoad implements Listener {

    @EventHandler
    public void onLoad(ItemsAdderLoadDataEvent event) {

        //FONT IMAGES
        if(BetterHudAPI.getFontImagesDirectory() != null && BetterHudAPI.getFontImagesDirectory().isDirectory() && BetterHudAPI.getFontImagesDirectory().listFiles() != null) {

            for(File child : BetterHudAPI.getFontImagesDirectory().listFiles()) {

                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(child);

                String namespace = yaml.getString("info.namespace");

                BetterHudAPI.fontImageCharacters.clear();

                if(yaml.contains("font_images")) {
                    for(String name : yaml.getConfigurationSection("font_images").getKeys(false)) {

                        BetterHudAPI.fontImageCharacters.put(name, new FontImageWrapper(namespace + ":" + name));

                    }
                }

            }

        }

        //IMAGE ELEMENTS

        BetterHudAPI.getLoadedHuds().stream().filter(Objects::nonNull).forEach(hud -> hud.getElements().stream().filter(element -> element instanceof ImageElement).forEach(element -> {

            FontImageWrapper image = new FontImageWrapper(((ImageElement) element).getImageName() + "-" + element.getY() + "_" + element.getScale());
            if(image != null && image.exists()) {

                ((ImageElement) element).setWidth(image.getWidth());

            } else {
                hud.removeElement(element);
            }

        }));


    }

}
