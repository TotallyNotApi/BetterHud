package cz.apigames.betterhud.plugin_old.Events;

import cz.apigames.betterhud.plugin_old.Hud.Editor.EditMode;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import cz.apigames.betterhud.plugin_old.Hud.HudPart;
import cz.apigames.betterhud.plugin_old.Utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EditorEvents implements Listener {

    @EventHandler
    public void onMove(PlayerInteractEvent event) {

        if(event.getItem() != null) {

            if(EditMode.getByPlayer(event.getPlayer()) != null) {

                event.setCancelled(true);
                ItemStack item = event.getItem();
                Player player = event.getPlayer();
                EditMode mode = EditMode.getByPlayer(player);

                //ADD
                if(item.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {

                    //1
                    if(item.getItemMeta().getDisplayName().equals(MessageUtils.colorize("&aMove by 1 &f>"))) {
                        add(mode, 1);
                    }
                    //5
                    if(item.getItemMeta().getDisplayName().equals(MessageUtils.colorize("&aMove by 5 &f>>"))) {
                        add(mode, 5);
                    }
                    //10
                    if(item.getItemMeta().getDisplayName().equals(MessageUtils.colorize("&aMove by 10 &f>>>"))) {
                        add(mode, 10);
                    }
                }
                //REMOVE
                else if(item.getType().equals(Material.RED_STAINED_GLASS_PANE)) {

                    //1
                    if(item.getItemMeta().getDisplayName().equals(MessageUtils.colorize("&f< &cMove by 1"))) {
                        add(mode, -1);
                    }
                    //5
                    if(item.getItemMeta().getDisplayName().equals(MessageUtils.colorize("&f<< &cMove by 5"))) {
                        add(mode, -5);
                    }
                    //10
                    if(item.getItemMeta().getDisplayName().equals(MessageUtils.colorize("&f<<< &cMove by 10"))) {
                        add(mode, -10);
                    }

                } else if(item.getType().equals(Material.BARRIER)) {

                    if(item.getItemMeta().getDisplayName().equals(MessageUtils.colorize("&4EXIT EDITOR MODE"))) {
                        mode.exit();
                    }

                }


            }

        }

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if(EditMode.getByPlayer((Player) event.getWhoClicked()) != null) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if(EditMode.getByPlayer(event.getPlayer()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if(event.getEntityType().equals(EntityType.PLAYER)) {

            if (EditMode.getByPlayer((Player) event.getEntity()) != null) {
                event.setCancelled(true);
            }
        }
    }

    private static void add(EditMode mode, int amount) {

        if(Hud.getByName(mode.getEditingHudName()) != null) {

            Hud hud = Hud.getByName(mode.getEditingHudName());
            for(HudPart part : hud.parts) {
                if(part.getPartName().equals(mode.getEditingPartName())) {
                    part.setPositionX(part.getPositionX()+amount);
                }
            }

        }

    }

}
