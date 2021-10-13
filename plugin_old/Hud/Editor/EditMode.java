package cz.apigames.betterhud.plugin_old.Hud.Editor;

import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import cz.apigames.betterhud.plugin_old.Hud.HudPart;
import cz.apigames.betterhud.plugin_old.Utils.ItemUtils;
import cz.apigames.betterhud.plugin_old.Utils.MessageUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class EditMode {

    private static HashMap<Player, EditMode> editors = new HashMap<>();
    private static HashMap<Player, ItemStack[]> inventories = new HashMap<>();

    private Player player;
    private String editingHudName;
    private String editingPartName;

    public EditMode(Player player) {

        if(!editors.containsKey(player)) {
            this.player = player;
            addEditor();
            player.sendMessage(MessageUtils.getMessage("editor-enter"));
            sendTextMenu("choose-hud");
        }

    }

    public static EditMode getByPlayer(Player player) {
        return editors.get(player);
    }

    public void addEditor() {
        if(!editors.containsKey(player)) {
            editors.put(player, this);
            inventories.put(player, player.getInventory().getContents());
            player.getInventory().clear();

            player.getInventory().setItem(4, ItemUtils.getControlItem(Material.BARRIER, 1, "&4EXIT EDITOR MODE"));
        }
    }

    public void removeEditor() {
        if(editors.containsKey(player)) {
            editors.remove(player);
            player.getInventory().setContents(inventories.get(player));
            inventories.remove(player);
        }
    }

    public void exit() {
        removeEditor();
        player.sendMessage(MessageUtils.getMessage("editor-leave"));
    }

    public static void exitAll() {

        for(EditMode edit : editors.values()) {
            edit.exit();
        }

    }

    public void setEditingHudName(String editingHudName) {
        this.editingHudName = editingHudName;
        if(Hud.getByName(editingHudName) != null) {
            new Display(player, editingHudName);
        }
    }

    public void setEditingPartName(String editingPartName) {
        this.editingPartName = editingPartName;
    }

    public String getEditingHudName() {
        return editingHudName;
    }

    public String getEditingPartName() {
        return editingPartName;
    }

    public void sendTextMenu(String menu) {

        if(menu.equalsIgnoreCase("choose-hud")) {

            player.sendMessage(MessageUtils.colorize("&8-»-------------------------------------------------«-"));
            player.sendMessage(" ");
            player.sendMessage(MessageUtils.colorize("&eChoose hud:"));

            for(Hud hud : Hud.getHuds()) {

                if(hud.getDisplayType().equals(Display.Type.ACTIONBAR) || hud.getDisplayType().equals(Display.Type.BOSSBAR)) {

                    net.md_5.bungee.api.chat.TextComponent hudLine = new net.md_5.bungee.api.chat.TextComponent(MessageUtils.colorize("&8- &3"+hud.getName()));
                    hudLine.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bh edit "+hud.getName()));
                    hudLine.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageUtils.colorize("&6Click to edit"))));

                    player.spigot().sendMessage(hudLine);

                }

            }

            net.md_5.bungee.api.chat.TextComponent exitBtn = new net.md_5.bungee.api.chat.TextComponent(MessageUtils.colorize("&c⏎ Exit"));
            exitBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bh editorExit"));
            exitBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageUtils.colorize("&cClick to exit editor"))));
            player.sendMessage(" ");
            player.spigot().sendMessage(exitBtn);

            player.sendMessage(" ");
            player.sendMessage(MessageUtils.colorize("&8-»-------------------------------------------------«-"));
            player.sendMessage(MessageUtils.colorize("&eALERT &8» &cThis function is not working properly sometimes. I'm working on fix!"));

        }
        if(menu.equalsIgnoreCase("choose-part")) {

            if(editingHudName != null) {
                if(Hud.getByName(editingHudName) != null) {
                    player.sendMessage(MessageUtils.colorize("&8-»-------------------------------------------------«-"));
                    player.sendMessage(" ");
                    player.sendMessage(MessageUtils.colorize("&eChoosed hud: &f"+editingHudName));
                    player.sendMessage(" ");
                    player.sendMessage(MessageUtils.colorize("&eChoose part:"));

                    for(HudPart part : Hud.getByName(editingHudName).parts) {

                        net.md_5.bungee.api.chat.TextComponent partLine = new net.md_5.bungee.api.chat.TextComponent(MessageUtils.colorize("&8- &3"+part.getPartName()));
                        partLine.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bh editPart "+editingHudName+" "+part.getPartName()));
                        partLine.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageUtils.colorize("&6Click to edit"))));

                        player.spigot().sendMessage(partLine);

                    }

                    net.md_5.bungee.api.chat.TextComponent backBtn = new net.md_5.bungee.api.chat.TextComponent(MessageUtils.colorize("&c⏎ Back"));
                    backBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bh editorBackHud"));
                    backBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageUtils.colorize("&cClick to go back"))));
                    player.sendMessage(" ");

                    player.spigot().sendMessage(backBtn);

                    player.sendMessage(" ");
                    player.sendMessage(MessageUtils.colorize("&8-»-------------------------------------------------«-"));
                }

            }

        }
        if(menu.equalsIgnoreCase("editing-part")) {
            player.sendMessage(MessageUtils.colorize("&8-»-------------------------------------------------«-"));
            player.sendMessage(" ");
            player.sendMessage(MessageUtils.colorize("&eChoosed hud: &f"+editingHudName));
            player.sendMessage(MessageUtils.colorize("&eYou are editing part: &f"+editingPartName));

            net.md_5.bungee.api.chat.TextComponent saveBtn = new net.md_5.bungee.api.chat.TextComponent(MessageUtils.colorize("&a✓ Save"));
            saveBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bh editorSavePart"));
            saveBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageUtils.colorize("&c&nWarning\n&cThis will delete all\n&call the comments inside config!\n\n&2Click to save this part"))));

            net.md_5.bungee.api.chat.TextComponent backBtn = new net.md_5.bungee.api.chat.TextComponent(MessageUtils.colorize("&c⏎ Back"));
            backBtn.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bh editorBackPart"));
            backBtn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageUtils.colorize("&cClick to go back"))));
            player.sendMessage(" ");
            player.spigot().sendMessage(saveBtn);
            player.spigot().sendMessage(backBtn);

            player.sendMessage(" ");
            player.sendMessage(MessageUtils.colorize("&8-»-------------------------------------------------«-"));
        }

    }

    public void giveEditorInventory() {

        Inventory inv = player.getInventory();

        inv.setItem(0, ItemUtils.getControlItem(Material.RED_STAINED_GLASS_PANE, 1, "&f< &cMove by 1"));
        inv.setItem(1, ItemUtils.getControlItem(Material.RED_STAINED_GLASS_PANE, 5, "&f<< &cMove by 5"));
        inv.setItem(2, ItemUtils.getControlItem(Material.RED_STAINED_GLASS_PANE, 10, "&f<<< &cMove by 10"));

        inv.setItem(4, ItemUtils.getControlItem(Material.BARRIER, 1, "&4EXIT EDITOR MODE"));

        inv.setItem(6, ItemUtils.getControlItem(Material.LIME_STAINED_GLASS_PANE, 10, "&aMove by 10 &f>>>"));
        inv.setItem(7, ItemUtils.getControlItem(Material.LIME_STAINED_GLASS_PANE, 5, "&aMove by 5 &f>>"));
        inv.setItem(8, ItemUtils.getControlItem(Material.LIME_STAINED_GLASS_PANE, 1, "&aMove by 1 &f>"));

    }

    public void removeEditorInventory() {

        Inventory inv = player.getInventory();

        inv.setItem(0, new ItemStack(Material.AIR));
        inv.setItem(1, new ItemStack(Material.AIR));
        inv.setItem(2, new ItemStack(Material.AIR));

        inv.setItem(4, ItemUtils.getControlItem(Material.BARRIER, 1, "&4EXIT EDITOR MODE"));

        inv.setItem(6, new ItemStack(Material.AIR));
        inv.setItem(7, new ItemStack(Material.AIR));
        inv.setItem(8, new ItemStack(Material.AIR));

    }
}
