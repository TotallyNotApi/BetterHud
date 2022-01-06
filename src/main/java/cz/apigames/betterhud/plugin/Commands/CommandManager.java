package cz.apigames.betterhud.plugin.Commands;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Displays.Display;
import cz.apigames.betterhud.api.Displays.DisplayType;
import cz.apigames.betterhud.api.Elements.Element;
import cz.apigames.betterhud.api.Hud;
import cz.apigames.betterhud.api.Utils.MessageUtils;
import cz.apigames.betterhud.api.Utils.ToggleCommand;
import cz.apigames.betterhud.plugin.Utils.ConfigManager;
import cz.apigames.betterhud.plugin.Utils.FileUtils;
import cz.apigames.betterhud.plugin.Utils.JsonMessage;
import cz.apigames.betterhud.plugin.Utils.TextureExtractor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CommandManager implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(command.getName().equalsIgnoreCase("bh") || command.getName().equalsIgnoreCase("betterhud")) {

            if(args.length > 0) {

                //SHOW
                if(args[0].equalsIgnoreCase("show")) {

                    Bukkit.getScheduler().runTaskAsynchronously(BetterHud.getPlugin(), (Runnable) () -> {

                        if(sender.hasPermission("betterhud.command.show")) {

                            if(args.length > 1) {

                                if(args.length > 2) {

                                    if(args.length > 3) {

                                        if(args[1].equalsIgnoreCase("all")) {
                                            //ALL

                                            if(BetterHud.getAPI().hudExists(args[2])) {

                                                try {
                                                    DisplayType displayType = DisplayType.valueOf(args[3]);

                                                    for(Player target : Bukkit.getOnlinePlayers()) {

                                                        try {
                                                            BetterHud.getAPI().getHud(args[2]).renderFor(target, displayType);
                                                        } catch (IllegalStateException e) {
                                                            sender.sendMessage(BetterHud.getMessage("show-error"));
                                                            return;
                                                        }

                                                    }

                                                    sender.sendMessage(BetterHud.getMessage("show-all")
                                                            .replace("{hudName}", args[2]));

                                                } catch (IllegalArgumentException e) {
                                                    sender.sendMessage(BetterHud.getMessage("unknown-display"));
                                                }

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                            }

                                        } else {
                                            //TARGET

                                            Player target = Bukkit.getPlayerExact(args[1]);
                                            if(target != null) {
                                                //VALID PLAYER

                                                if(BetterHud.getAPI().hudExists(args[2])) {

                                                    try {
                                                        DisplayType displayType = DisplayType.valueOf(args[3]);

                                                        try {
                                                            if(BetterHud.getAPI().getHud(args[2]).renderFor(target, displayType)) {
                                                                sender.sendMessage(BetterHud.getMessage("show-player")
                                                                        .replace("{hudName}", args[2]).replace("{player}", target.getName()));
                                                            } else {
                                                                sender.sendMessage(BetterHud.getMessage("show-condition-error"));
                                                            }
                                                        } catch (IllegalStateException | NumberFormatException e) {

                                                            if(e instanceof NumberFormatException) {
                                                                sender.sendMessage(BetterHud.getMessage("show-error").replace("{error}", ((NumberFormatException) e).getMessage()));
                                                            } else {
                                                                sender.sendMessage(BetterHud.getMessage("show-display-active"));
                                                            }
                                                        }

                                                    } catch (IllegalArgumentException e) {
                                                        sender.sendMessage(BetterHud.getMessage("unknown-display"));
                                                    }

                                                } else {
                                                    sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                                }

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-player"));
                                            }
                                        }

                                    } else {
                                        sender.sendMessage(BetterHud.getMessage("no-display"));
                                    }

                                } else {
                                    sender.sendMessage(BetterHud.getMessage("no-hud"));
                                }

                            } else {
                                sender.sendMessage(BetterHud.getMessage("no-player"));
                            }

                        } else {
                            sender.sendMessage(BetterHud.getMessage("no-permission"));
                        }

                    });

                }

                //HIDE
                else if(args[0].equalsIgnoreCase("hide")) {

                    if(sender.hasPermission("betterhud.command.hide")) {

                        if(args.length > 1) {

                            if(args.length > 2) {

                                if(args[1].equalsIgnoreCase("all")) {
                                    //ALL

                                    if(BetterHud.getAPI().hudExists(args[2])) {

                                        for(Player target : Bukkit.getOnlinePlayers()) {
                                            BetterHud.getAPI().getHud(args[2]).hide(target);
                                        }

                                        sender.sendMessage(BetterHud.getMessage("hide-all")
                                                .replace("{hudName}", args[2]));

                                    } else {
                                        sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                    }

                                } else {
                                    //TARGET

                                    Player target = Bukkit.getPlayerExact(args[1]);
                                    if(target != null) {
                                        //VALID PLAYER

                                        if(BetterHud.getAPI().hudExists(args[2])) {

                                            BetterHud.getAPI().getHud(args[2]).hide(target);

                                            sender.sendMessage(BetterHud.getMessage("hide-player")
                                                    .replace("{hudName}", args[2]).replace("{player}", target.getName()));

                                        } else {
                                            sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                        }

                                    } else {
                                        sender.sendMessage(BetterHud.getMessage("unknown-player"));
                                    }
                                }

                            } else {
                                sender.sendMessage(BetterHud.getMessage("no-hud"));
                            }

                        } else {
                            sender.sendMessage(BetterHud.getMessage("no-player"));
                        }

                    } else {
                        sender.sendMessage(BetterHud.getMessage("no-permission"));
                    }

                }

                //SET VALUE
                else if(args[0].equalsIgnoreCase("setValue")) {

                    if(sender.hasPermission("betterhud.command.setvalue")) {

                        //PLAYER
                        if(args.length > 1) {

                            //HUD
                            if(args.length > 2) {

                                //ELEMENT
                                if(args.length > 3) {

                                    //VALUE
                                    if(args.length > 4) {

                                        if(args[1].equalsIgnoreCase("all")) {
                                            //ALL

                                            if(BetterHud.getAPI().hudExists(args[2])) {

                                                Hud hud = BetterHud.getAPI().getHud(args[2]);
                                                if(hud.getElement(args[3]).isPresent()) {
                                                    Element element = hud.getElement(args[3]).get();

                                                    element.resetAllValues();

                                                    StringBuilder value = new StringBuilder();
                                                    for(int i = 4; i < args.length; i++) {
                                                        value.append(args[i]);
                                                        if(i != args.length-1) {
                                                            value.append(" ");
                                                        }
                                                    }
                                                    element.setValue(value.toString());

                                                    sender.sendMessage(BetterHud.getMessage("setvalue-all")
                                                            .replace("{element}", element.getName()));

                                                } else {
                                                    sender.sendMessage(BetterHud.getMessage("unknown-element"));
                                                }

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                            }

                                        } else {
                                            //TARGET

                                            Player target = Bukkit.getPlayerExact(args[1]);
                                            if(target != null) {
                                                //VALID PLAYER

                                                if(BetterHud.getAPI().hudExists(args[2])) {

                                                    Hud hud = BetterHud.getAPI().getHud(args[2]);
                                                    if(hud.getElement(args[3]).isPresent()) {

                                                        Element element = hud.getElement(args[3]).get();

                                                        StringBuilder value = new StringBuilder();
                                                        for(int i = 4; i < args.length; i++) {
                                                            value.append(args[i]);
                                                            if(i != args.length-1) {
                                                                value.append(" ");
                                                            }
                                                        }
                                                        element.setValue(target, value.toString());

                                                        sender.sendMessage(BetterHud.getMessage("setvalue-player")
                                                                .replace("{element}", element.getName())
                                                                .replace("{player}", target.getName()));

                                                    } else {
                                                        sender.sendMessage(BetterHud.getMessage("unknown-element"));
                                                    }

                                                } else {
                                                    sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                                }

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-player"));
                                            }
                                        }

                                    } else {
                                        sender.sendMessage(BetterHud.getMessage("no-value"));
                                    }

                                } else {
                                    sender.sendMessage(BetterHud.getMessage("no-element"));
                                }

                            } else {
                                sender.sendMessage(BetterHud.getMessage("no-hud"));
                            }

                        } else {
                            sender.sendMessage(BetterHud.getMessage("no-player"));
                        }

                    } else {
                        sender.sendMessage(BetterHud.getMessage("no-permission"));
                    }

                }

                //GET VALUE
                else if(args[0].equalsIgnoreCase("getValue")) {

                    if(sender.hasPermission("betterhud.command.getvalue")) {

                        //PLAYER
                        if(args.length > 1) {

                            //HUD
                            if(args.length > 2) {

                                //ELEMENT
                                if(args.length > 3) {

                                    Player target = Bukkit.getPlayerExact(args[1]);
                                    if(target != null) {
                                        //VALID PLAYER

                                        if(BetterHud.getAPI().hudExists(args[2])) {

                                            Hud hud = BetterHud.getAPI().getHud(args[2]);
                                            if(hud.getElement(args[3]).isPresent()) {

                                                Element element = hud.getElement(args[3]).get();

                                                sender.sendMessage(BetterHud.getMessage("getvalue-player")
                                                        .replace("{element}", element.getName())
                                                        .replace("{player}", target.getName())
                                                        .replace("{value}", element.getValue(target)));

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-element"));
                                            }

                                        } else {
                                            sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                        }

                                    } else {
                                        sender.sendMessage(BetterHud.getMessage("unknown-player"));
                                    }

                                } else {
                                    sender.sendMessage(BetterHud.getMessage("no-element"));
                                }

                            } else {
                                sender.sendMessage(BetterHud.getMessage("no-hud"));
                            }

                        } else {
                            sender.sendMessage(BetterHud.getMessage("no-player"));
                        }

                    } else {
                        sender.sendMessage(BetterHud.getMessage("no-permission"));
                    }

                }

                //RESET VALUE
                else if(args[0].equalsIgnoreCase("resetValue")) {

                    if(sender.hasPermission("betterhud.command.resetvalue")) {

                        //PLAYER
                        if(args.length > 1) {

                            //HUD
                            if(args.length > 2) {

                                //ELEMENT
                                if(args.length > 3) {

                                    if(args[1].equalsIgnoreCase("all")) {
                                        //ALL

                                        if(BetterHud.getAPI().hudExists(args[2])) {

                                            Hud hud = BetterHud.getAPI().getHud(args[2]);
                                            if(hud.getElement(args[3]).isPresent()) {
                                                Element element = hud.getElement(args[3]).get();

                                                element.resetAllValues();

                                                sender.sendMessage(BetterHud.getMessage("resetvalue-all")
                                                        .replace("{element}", element.getName()));

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-element"));
                                            }

                                        } else {
                                            sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                        }

                                    } else {
                                        //TARGET

                                        Player target = Bukkit.getPlayerExact(args[1]);
                                        if(target != null) {
                                            //VALID PLAYER

                                            if(BetterHud.getAPI().hudExists(args[2])) {

                                                Hud hud = BetterHud.getAPI().getHud(args[2]);
                                                if(hud.getElement(args[3]).isPresent()) {

                                                    Element element = hud.getElement(args[3]).get();

                                                    element.resetValue(target);

                                                    sender.sendMessage(BetterHud.getMessage("resetvalue-player")
                                                            .replace("{element}", element.getName())
                                                            .replace("{player}", target.getName()));

                                                } else {
                                                    sender.sendMessage(BetterHud.getMessage("unknown-element"));
                                                }

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                            }

                                        } else {
                                            sender.sendMessage(BetterHud.getMessage("unknown-player"));
                                        }
                                    }

                                } else {
                                    sender.sendMessage(BetterHud.getMessage("no-element"));
                                }

                            } else {
                                sender.sendMessage(BetterHud.getMessage("no-hud"));
                            }

                        } else {
                            sender.sendMessage(BetterHud.getMessage("no-player"));
                        }

                    } else {
                        sender.sendMessage(BetterHud.getMessage("no-permission"));
                    }

                }

                //SHOW ELEMENT
                else if(args[0].equalsIgnoreCase("showElement")) {

                    if(sender.hasPermission("betterhud.command.showelement")) {

                        //PLAYER
                        if(args.length > 1) {

                            //HUD
                            if(args.length > 2) {

                                //ELEMENT
                                if(args.length > 3) {

                                    if(args[1].equalsIgnoreCase("all")) {
                                        //ALL

                                        if(BetterHud.getAPI().hudExists(args[2])) {

                                            Hud hud = BetterHud.getAPI().getHud(args[2]);
                                            if(hud.getElement(args[3]).isPresent()) {
                                                Element element = hud.getElement(args[3]).get();

                                                for(Player target : Bukkit.getOnlinePlayers()) {
                                                    element.setVisibility(target, true);
                                                }

                                                sender.sendMessage(BetterHud.getMessage("showelement-all")
                                                        .replace("{element}", element.getName()));

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-element"));
                                            }

                                        } else {
                                            sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                        }

                                    } else {
                                        //TARGET

                                        Player target = Bukkit.getPlayerExact(args[1]);
                                        if(target != null) {
                                            //VALID PLAYER

                                            if(BetterHud.getAPI().hudExists(args[2])) {

                                                Hud hud = BetterHud.getAPI().getHud(args[2]);
                                                if(hud.getElement(args[3]).isPresent()) {

                                                    Element element = hud.getElement(args[3]).get();

                                                    element.setVisibility(target, true);

                                                    sender.sendMessage(BetterHud.getMessage("showelement-player")
                                                            .replace("{element}", element.getName())
                                                            .replace("{player}", target.getName()));

                                                } else {
                                                    sender.sendMessage(BetterHud.getMessage("unknown-element"));
                                                }

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                            }

                                        } else {
                                            sender.sendMessage(BetterHud.getMessage("unknown-player"));
                                        }
                                    }

                                } else {
                                    sender.sendMessage(BetterHud.getMessage("no-element"));
                                }

                            } else {
                                sender.sendMessage(BetterHud.getMessage("no-hud"));
                            }

                        } else {
                            sender.sendMessage(BetterHud.getMessage("no-player"));
                        }

                    } else {
                        sender.sendMessage(BetterHud.getMessage("no-permission"));
                    }

                }

                //HIDE ELEMENT
                else if(args[0].equalsIgnoreCase("hideElement")) {

                    if(sender.hasPermission("betterhud.command.hideelement")) {

                        //PLAYER
                        if(args.length > 1) {

                            //HUD
                            if(args.length > 2) {

                                //ELEMENT
                                if(args.length > 3) {

                                    if(args[1].equalsIgnoreCase("all")) {
                                        //ALL

                                        if(BetterHud.getAPI().hudExists(args[2])) {

                                            Hud hud = BetterHud.getAPI().getHud(args[2]);
                                            if(hud.getElement(args[3]).isPresent()) {
                                                Element element = hud.getElement(args[3]).get();

                                                for(Player target : Bukkit.getOnlinePlayers()) {
                                                    element.setVisibility(target, false);
                                                }

                                                sender.sendMessage(BetterHud.getMessage("hideelement-all")
                                                        .replace("{element}", element.getName()));

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-element"));
                                            }

                                        } else {
                                            sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                        }

                                    } else {
                                        //TARGET

                                        Player target = Bukkit.getPlayerExact(args[1]);
                                        if(target != null) {
                                            //VALID PLAYER

                                            if(BetterHud.getAPI().hudExists(args[2])) {

                                                Hud hud = BetterHud.getAPI().getHud(args[2]);
                                                if(hud.getElement(args[3]).isPresent()) {

                                                    Element element = hud.getElement(args[3]).get();

                                                    element.setVisibility(target, false);

                                                    sender.sendMessage(BetterHud.getMessage("hideelement-player")
                                                            .replace("{element}", element.getName())
                                                            .replace("{player}", target.getName()));

                                                } else {
                                                    sender.sendMessage(BetterHud.getMessage("unknown-element"));
                                                }

                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                            }

                                        } else {
                                            sender.sendMessage(BetterHud.getMessage("unknown-player"));
                                        }
                                    }

                                } else {
                                    sender.sendMessage(BetterHud.getMessage("no-element"));
                                }

                            } else {
                                sender.sendMessage(BetterHud.getMessage("no-hud"));
                            }

                        } else {
                            sender.sendMessage(BetterHud.getMessage("no-player"));
                        }

                    } else {
                        sender.sendMessage(BetterHud.getMessage("no-permission"));
                    }

                }

                //SET X
                else if(args[0].equalsIgnoreCase("setX")) {

                    if(sender.hasPermission("betterhud.command.setx")) {

                        //HUD
                        if(args.length > 1) {

                            //ELEMENT
                            if(args.length > 2) {

                                //VALUE
                                if(args.length > 3) {

                                    if(BetterHud.getAPI().hudExists(args[1])) {

                                        Hud hud = BetterHud.getAPI().getHud(args[1]);
                                        if(hud.getElement(args[2]).isPresent()) {
                                            Element element = hud.getElement(args[2]).get();

                                            if(isNumber(args[3])) {
                                                element.setX((int) Double.parseDouble(args[3]));

                                                sender.sendMessage(BetterHud.getMessage("set-x")
                                                        .replace("{element}", element.getName())
                                                        .replace("{value}", args[3]));
                                            } else {
                                                sender.sendMessage(BetterHud.getMessage("invalid-number"));
                                            }

                                        } else {
                                            sender.sendMessage(BetterHud.getMessage("unknown-element"));
                                        }

                                    } else {
                                        sender.sendMessage(BetterHud.getMessage("unknown-hud"));
                                    }

                                } else {
                                    sender.sendMessage(BetterHud.getMessage("no-value"));
                                }

                            } else {
                                sender.sendMessage(BetterHud.getMessage("no-element"));
                            }

                        } else {
                            sender.sendMessage(BetterHud.getMessage("no-hud"));
                        }

                    } else {
                        sender.sendMessage(BetterHud.getMessage("no-permission"));
                    }

                }

                //RELOAD
                else if(args[0].equalsIgnoreCase("reload")) {

                    if(sender.hasPermission("betterhud.command.reload")) {

                        long time = System.currentTimeMillis();

                        //TEMP CACHE
                        HashMap<Player, HashMap<DisplayType, String>> cachedActiveHuds = new HashMap<>();

                        Display.getDisplays().forEach(display -> {

                            HashMap<DisplayType, String> huds;
                            if(cachedActiveHuds.containsKey(display.getPlayer())) {
                                huds = cachedActiveHuds.get(display.getPlayer());
                            } else {
                                huds = new HashMap<>();
                            }

                            huds.put(DisplayType.getDisplayType(display), display.getHud().getName());
                            cachedActiveHuds.put(display.getPlayer(), huds);

                        });

                        BetterHud.getAPI().unload();

                        ConfigManager.reloadConfig("config.yml");
                        ConfigManager.reloadConfig("messages.yml");
                        ConfigManager.reloadConfig("characters.yml");

                        //TOGGLE COMMAND MESSAGES
                        ToggleCommand.setEnableMessage(ConfigManager.getConfig("messages.yml").getString("messages.toggle-custom-on", ""));
                        ToggleCommand.setDisableMessage(ConfigManager.getConfig("messages.yml").getString("messages.toggle-custom-off", ""));

                        List<String> errors = BetterHud.getAPI().load(new File(BetterHud.getPlugin().getDataFolder(), "config.yml"), true);
                        Future<Boolean> FontImageFiles_success = BetterHud.getAPI().generateFontImageFiles(new File(BetterHud.getPlugin().getDataFolder(), "characters.yml"), new File("plugins/ItemsAdder/data/items_packs/betterhud"));

                        try {
                            if(FontImageFiles_success.get(5, TimeUnit.SECONDS)) {

                                //ASYNC
                                Bukkit.getScheduler().runTaskAsynchronously(BetterHud.getPlugin(), () -> {

                                    boolean ia_reload = false;

                                    Set<String> updatedChecksums = new HashSet<>();
                                    for(File child : BetterHudAPI.getFontImagesDirectory().listFiles()) {

                                        String checksum = FileUtils.checksum(child);
                                        updatedChecksums.add(checksum);

                                        if(!BetterHud.checksums.contains(checksum)) {
                                            ia_reload = true;
                                        }

                                    }
                                    BetterHud.checksums.clear();
                                    BetterHud.checksums.addAll(updatedChecksums);

                                    if(ia_reload) {
                                        Bukkit.getScheduler().runTask(BetterHud.getPlugin(), () -> {
                                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "iazip");
                                            sender.sendMessage(BetterHud.getMessage("reload-itemsadder"));
                                        });
                                    }

                                    //SHOW ACTIVE DISPLAYS
                                    cachedActiveHuds.forEach((player, huds) -> huds.forEach((displayType, s) -> {

                                        if(BetterHud.getAPI().hudExists(s)) {
                                            Display.createDisplay(player, BetterHud.getAPI().getHud(s), displayType);
                                        }

                                    }));

                                    cachedActiveHuds.clear();

                                });

                                //ERROR MESSAGE
                                if(!errors.isEmpty()) {
                                    BetterHud.sendErrorToConsole("========================================");
                                    BetterHud.sendErrorToConsole("BetterHud - Found configuration errors");
                                    BetterHud.sendErrorToConsole(" ");
                                    errors.forEach(BetterHud::sendErrorToConsole);
                                    BetterHud.sendErrorToConsole(" ");
                                    BetterHud.sendErrorToConsole("========================================");
                                    sender.sendMessage(BetterHud.getMessage("reload-error"));
                                    return true;
                                }

                                sender.sendMessage(BetterHud.getMessage("reload-successful").replace("{time}", String.valueOf(System.currentTimeMillis()-time)));

                            }
                        } catch (InterruptedException | ExecutionException e) {
                            BetterHud.error("An error occurred while waiting for FontImage file generation task completion.", e);
                            sender.sendMessage(BetterHud.getMessage("reload-error"));
                            return true;
                        } catch (TimeoutException e) {
                            BetterHud.error("FontImage files generation took too long! Reload task was terminated to keep thread safe.", e);
                            sender.sendMessage(BetterHud.getMessage("reload-error"));
                            return true;
                        }

                    } else {
                        sender.sendMessage(BetterHud.getMessage("no-permission"));
                    }

                }

                //EXPORT TEXTURES
                else if(args[0].equalsIgnoreCase("extractTextures")) {

                    if(sender.hasPermission("betterhud.command.extracttextures")) {

                        try {
                            if(TextureExtractor.extract()) {
                                sender.sendMessage(BetterHud.getMessage("extract-textures-success"));
                            } else {
                                sender.sendMessage(BetterHud.getMessage("extract-textures-error"));
                            }
                        } catch (IOException e) {
                            BetterHud.error("Failed to extract textures from JAR file!", e);
                            sender.sendMessage(BetterHud.getMessage("extract-textures-error"));
                        }

                    } else {
                        sender.sendMessage(BetterHud.getMessage("no-permission"));
                    }

                }

                //TEST
                else if(args[0].equalsIgnoreCase("test")) {

                    Player player = (Player) sender;
                    if(!Display.getDisplays(player).isEmpty()) {
                        Display.getDisplays(player).forEach(display -> {
                            display.getHud().getElements().forEach(element -> {
                                player.sendMessage("-------------------------");
                                player.sendMessage("ElementName: "+element.getName());
                                player.sendMessage("X,Y: "+element.getX() + ";" + element.getY());
                                player.sendMessage("iX,iY: "+element.ix + ";" + element.iy);
                                player.sendMessage("width: "+element.calculateWidth(player));
                            });
                        });
                    }

                }

                //HELP
                else if(args[0].equalsIgnoreCase("help")) {
                    help(sender);
                } else {
                    help(sender);
                }

            } else {
                help(sender);
            }

        }

        return true;
    }

    private static void help(CommandSender sender) {

        sender.sendMessage(MessageUtils.colorize("&6&lBetterHud &f&lv"+ BetterHud.getVersion() + " &7&o(( By ApiGames ))"));
        sender.sendMessage(" ");

        if(sender instanceof Player) {

            Player player = (Player) sender;
            JsonMessage.sendMessage(player, "/bh show","&8- &e/bh show (player/all) (hud) (display)", "&bShow the hud for the player\n\n&7Permission: &ebetterhud.command.show\n\n&f&lEXAMPLES:\n&a/bh show ApiGames example_hud ACTIONBAR");
            JsonMessage.sendMessage(player,"/bh hide" ,"&8- &e/bh hide (player/all) (hud)", "&bHide the hud from the player\n\n&7Permission: &ebetterhud.command.hide\n\n&f&lEXAMPLES:\n&a/bh hide ApiGames example_hud");
            JsonMessage.sendMessage(player,"/bh setValue","&8- &e/bh setValue (player/all) (hud) (element) (value)", "&bChange displayed value\n\n&7Permission: &ebetterhud.command.setvalue\n\n&f&lEXAMPLES:\n&a/bh setValue ApiGames example_hud example_text TEST123");
            JsonMessage.sendMessage(player, "/bh getValue","&8- &e/bh getValue (player) (hud) (element)", "&bGet the per-player value of the element\n\n&7Permission: &ebetterhud.command.getvalue\n\n&f&lEXAMPLES:\n&a/bh getValue ApiGames example_hud example_text");
            JsonMessage.sendMessage(player, "/bh resetValue","&8- &e/bh resetValue (player/all) (hud) (element)", "&bReset the per-player value\n\n&7Permission: &ebetterhud.command.resetvalue\n\n&f&lEXAMPLES:\n&a/bh resetValue ApiGames example_hud");
            JsonMessage.sendMessage(player, "/bh showElement","&8- &e/bh showElement (player/all) (hud) (element)", "&bShow the element for the player\n\n&7Permission: &ebetterhud.command.showelement\n\n&f&lEXAMPLES:\n&a/bh showElement ApiGames example_hud example_text");
            JsonMessage.sendMessage(player, "/bh hideElement","&8- &e/bh hideElement (player/all) (hud) (element)", "&bHide the element from the player\n\n&7Permission: &ebetterhud.command.hideelement\n\n&f&lEXAMPLES:\n&a/bh hideElement ApiGames example_hud example_text");
            JsonMessage.sendMessage(player, "/bh setX","&8- &e/bh setX (hud) (element) (value)", "&bSet x-coordinate of element\n\n&7Permission: &ebetterhud.command.setx\n\n&f&lEXAMPLES:\n&a/bh setX example_hud example_text 120");
            JsonMessage.sendMessage(player, "/bh reload","&8- &e/bh reload", "&bReload the plugin\n\n&7Permission: &ebetterhud.command.reload");
            JsonMessage.sendMessage(player, "/bh report-bug","&8- &e/bh report-bug", "&bGenerate report log\n\n&7Permission: &ebetterhud.command.report-bug");
            JsonMessage.sendMessage(player, "/bh extractTextures","&8- &e/bh extractTextures", "&bExtract default BetterHud textures\n\n&7Permission: &ebetterhud.command.extracttextures");
            sender.sendMessage(" ");
            sender.sendMessage(MessageUtils.colorize("&eTIP &8» &fTry &ahovering &fover the command to see more info and examples!"));

        } else {
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh show (player/all) (hud) (display) &7- Show the hud for the player"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh hide (player/all) (hud) &7- Hide the hud from the player"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh setValue (player/all) (hud) (element) (value) &7- Change displayed value"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh getValue (player) (hud) (element) &7- Get the per-player value of the element"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh resetValue (player/all) (hud) (element) &7- Reset the per-player value"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh showElement (player/all) (hud) (element) &7- Show the element for the player"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh hideElement (player/all) (hud) (element) &7- Hide the element from the player"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh setX (hud) (element) (value) &7- Set x-coordinate of element"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh reload &7- Reload the plugin"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh report-bug &7- Generate report log"));
            sender.sendMessage(MessageUtils.colorize("&8- &e/bh extractTextures &7- Extract default BetterHud textures"));
        }

        sender.sendMessage(" ");
    }

    public static boolean isNumber(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
