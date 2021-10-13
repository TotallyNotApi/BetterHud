package cz.apigames.betterhud.plugin_old.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

    public static ItemStack getControlItem(Material material, int amount, String displayName) {

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.colorize(displayName));
        item.setItemMeta(meta);

        return item;
    }

}
