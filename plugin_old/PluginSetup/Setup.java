package cz.apigames.betterhud.plugin_old.PluginSetup;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Commands.ReloadCommand;
import cz.apigames.betterhud.plugin_old.Utils.FileUtils;
import cz.apigames.betterhud.plugin_old.Utils.MessageUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Setup {

    public static void welcomeMessage(Player player) {

        TextComponent startButton = new TextComponent(MessageUtils.colorize("&8[&a&lBEGIN SETUP&8]"));
        startButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bh runSetup"));
        startButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageUtils.colorize("&eClick to begin setup"))));

        TextComponent wiki = new TextComponent(MessageUtils.colorize("&eINFO &8» &7If you don't know how to install BetterHud with external-host, check this &6tutorial&7."));
        wiki.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://apigames.gitbook.io/betterhud/tutorials/first-install#external-host"));
        wiki.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageUtils.colorize("&eClick to open Wiki"))));

        ComponentBuilder componentBuilder = new ComponentBuilder();
        componentBuilder.append("                             ");
        componentBuilder.append(startButton);

        if(BetterHud.isIASelfHosted()) {
            player.sendMessage(MessageUtils.colorize("&8-»-------------------------------------------------«-"));
            player.sendMessage(" ");
            player.sendMessage(MessageUtils.colorize("&7                      Welcome to &6BetterHud's&7 setup"));
            player.sendMessage(" ");
            player.sendMessage(MessageUtils.colorize("&7                  Everything seems to be set up well."));
            player.sendMessage(MessageUtils.colorize("&7                    Please, click on the button below."));
            player.sendMessage(" ");
            player.spigot().sendMessage(componentBuilder.create());
            player.sendMessage(" ");
            player.sendMessage(MessageUtils.colorize("&8-»-------------------------------------------------«-"));
        } else {
            player.sendMessage(MessageUtils.colorize("&8-»-------------------------------------------------«-"));
            player.sendMessage(" ");
            player.sendMessage(MessageUtils.colorize("&7                      Welcome to &6BetterHud's&7 setup"));
            player.sendMessage(" ");
            player.sendMessage(MessageUtils.colorize("&7                It looks like you don't have self-host"));
            player.sendMessage(MessageUtils.colorize("&7               &aenabled&7. We strongly recommend to use"));
            player.sendMessage(MessageUtils.colorize("&7                self-host for hosting resource-pack."));
            player.sendMessage(" ");
            player.spigot().sendMessage(componentBuilder.create());
            player.sendMessage(" ");
            player.sendMessage(MessageUtils.colorize("&8-»-------------------------------------------------«-"));
            player.spigot().sendMessage(wiki);
        }

    }

    public static void startSetup(CommandSender initiator) {

        BetterHud.ConsoleError = false;

        if(BetterHud.isIASelfHosted()) {
            initiator.sendMessage(MessageUtils.colorize("&eBetterHud &8» &aExporting textures.."));
            FileUtils.exportTextures("ItemsAdder");
            if(BetterHud.ConsoleError) {
                initiator.sendMessage(MessageUtils.colorize("&eBetterHud &8» &cExporting textures failed! Please, check console for more information."));
                return;
            }
            initiator.sendMessage(MessageUtils.colorize("&eBetterHud &8» &aCreating namespace.."));
        } else {
            initiator.sendMessage(MessageUtils.colorize("&eBetterHud &8» &aExporting textures.."));
            FileUtils.exportTextures("BetterHud");
            FileUtils.zipTextures();
            if(BetterHud.ConsoleError) {
                initiator.sendMessage(MessageUtils.colorize("&eBetterHud &8» &cExporting textures failed! Please, check console for more information."));
                return;
            }
            initiator.sendMessage(MessageUtils.colorize("&eBetterHud &8» &aCreating new archive inside BetterHud folder.."));
        }

        initiator.sendMessage(MessageUtils.colorize("&eBetterHud &8» &aGenerating items_packs file.."));
        FileUtils.generateIPFile();
        if(BetterHud.ConsoleError) {
            initiator.sendMessage(MessageUtils.colorize("&eBetterHud &8» &cGenerating items_packs file failed! Please, check console for more information."));
            return;
        }
        initiator.sendMessage(MessageUtils.colorize("&eBetterHud &8» &aReloading BetterHud.."));
        ReloadCommand.reloadAll(initiator);
        initiator.sendMessage(MessageUtils.colorize("&eBetterHud &8» &aPlugin set-up done!"));
    }

}
