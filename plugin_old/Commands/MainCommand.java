package cz.apigames.betterhud.plugin_old.Commands;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.DisplayRunnable;
import cz.apigames.betterhud.plugin_old.Hud.Editor.EditMode;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import cz.apigames.betterhud.plugin_old.Hud.HudPart;
import cz.apigames.betterhud.plugin_old.PluginSetup.Setup;
import cz.apigames.betterhud.plugin_old.Utils.FileUtils;
import cz.apigames.betterhud.plugin_old.Utils.Logger;
import cz.apigames.betterhud.plugin_old.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(command.getName().equalsIgnoreCase("bh") || command.getName().equalsIgnoreCase("betterhud")) {
            if(args.length > 0) {

                //RUN SETUP
                if(args[0].equalsIgnoreCase("runSetup")) {

                    if(sender.isOp()) {
                        if(!BetterHud.isPluginSetup()) {
                            Setup.startSetup(sender);
                        }
                    }

                }

                //REPORT-BUG
                else if(args[0].equalsIgnoreCase("report-bug")) {

                    BetterHud.ConsoleError = false;

                    BetterHud.debug("Creating report log..");
                    Logger.createReportFile();
                    BetterHud.debug("Creating archive..");
                    FileUtils.zipPluginFolder();

                    if(BetterHud.ConsoleError) {
                        BetterHud.debug("Report successful");
                        sender.sendMessage(MessageUtils.colorize(" &8» &cSomething failed, please check console and errors.txt for more information."));
                    } else {
                        BetterHud.debug("Reporting bug failed");
                        sender.sendMessage(MessageUtils.colorize(" &8» &aReport file has been successfully created! Please, upload '&2report.zip&a' to your support ticket."));
                    }

                }

                if(!BetterHud.isPluginSetup()) {

                    sender.sendMessage(MessageUtils.colorize(" &8» &cYou have to complete the plugin setup, firstly!"));
                    return true;

                }

                // ################ \\
                //  EDITOR SECTION  \\
                // ################ \\

                //EDITOR
                else if(args[0].equalsIgnoreCase("editor")) {

                    if(sender.hasPermission("betterhud.command.editor")) {

                        if(sender instanceof Player) {

                            Player player = (Player) sender;
                            if(EditMode.getByPlayer(player) != null) {
                                EditMode.getByPlayer(player).exit();
                            } else {
                                new EditMode(player);
                            }


                        } else {
                            sender.sendMessage(MessageUtils.getMessage("players-only"));
                        }

                    } else {
                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                    }

                }

                //EDITOR - choose hud
                else if(args[0].equalsIgnoreCase("edit")) {

                    if(sender.hasPermission("betterhud.command.editor")) {

                        if(sender instanceof Player) {

                            if(args.length > 1) {

                                Player player = (Player) sender;

                                if(EditMode.getByPlayer(player) != null) {

                                    if(Hud.getByName(args[1]) != null) {

                                        EditMode edit = EditMode.getByPlayer(player);
                                        edit.setEditingHudName(args[1]);
                                        edit.sendTextMenu("choose-part");

                                    }

                                }

                            }

                        } else {
                            sender.sendMessage(MessageUtils.getMessage("players-only"));
                        }

                    } else {
                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                    }

                }

                //EDITOR - choose part
                else if(args[0].equalsIgnoreCase("editPart")) {

                    if(sender.hasPermission("betterhud.command.editor")) {

                        if(sender instanceof Player) {

                            if(args.length > 2) {

                                Player player = (Player) sender;

                                if(EditMode.getByPlayer(player) != null) {

                                    if(Hud.getByName(args[1]) != null) {

                                        EditMode edit = EditMode.getByPlayer(player);
                                        edit.setEditingPartName(args[2]);
                                        edit.sendTextMenu("editing-part");
                                        edit.giveEditorInventory();

                                    }

                                }

                            }

                        } else {
                            sender.sendMessage(MessageUtils.getMessage("players-only"));
                        }

                    } else {
                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                    }

                }

                //EDITOR - backPart
                else if(args[0].equalsIgnoreCase("editorBackPart")) {

                    if(sender.hasPermission("betterhud.command.editor")) {

                        if(sender instanceof Player) {

                            Player player = (Player) sender;

                            if(EditMode.getByPlayer(player) != null) {

                                EditMode edit = EditMode.getByPlayer(player);
                                edit.setEditingPartName(null);
                                edit.sendTextMenu("choose-part");
                                edit.removeEditorInventory();

                            }

                        } else {
                            sender.sendMessage(MessageUtils.getMessage("players-only"));
                        }

                    } else {
                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                    }

                }

                //EDITOR - backHud
                else if(args[0].equalsIgnoreCase("editorBackHud")) {

                    if(sender.hasPermission("betterhud.command.editor")) {

                        if(sender instanceof Player) {

                            Player player = (Player) sender;

                            if(EditMode.getByPlayer(player) != null) {

                                EditMode edit = EditMode.getByPlayer(player);
                                edit.setEditingHudName(null);
                                edit.sendTextMenu("choose-hud");

                            }

                        } else {
                            sender.sendMessage(MessageUtils.getMessage("players-only"));
                        }

                    } else {
                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                    }

                }

                //EDITOR - exit
                else if(args[0].equalsIgnoreCase("editorExit")) {

                    if(sender.hasPermission("betterhud.command.editor")) {

                        if(sender instanceof Player) {

                            Player player = (Player) sender;

                            if(EditMode.getByPlayer(player) != null) {

                                EditMode.getByPlayer(player).exit();

                            }

                        } else {
                            sender.sendMessage(MessageUtils.getMessage("players-only"));
                        }

                    } else {
                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                    }

                }

                //EDITOR - savePart
                else if(args[0].equalsIgnoreCase("editorSavePart")) {

                    if(sender.hasPermission("betterhud.command.editor")) {

                        if(sender instanceof Player) {

                            Player player = (Player) sender;

                            if(EditMode.getByPlayer(player) != null) {

                                EditMode mode = EditMode.getByPlayer(player);

                                if(Hud.getByName(mode.getEditingHudName()) != null) {

                                    Hud hud = Hud.getByName(mode.getEditingHudName());
                                    for(HudPart part : hud.parts) {
                                        if(part.getPartName().equals(mode.getEditingPartName())) {
                                            part.save();

                                            player.sendMessage(MessageUtils.getMessage("editor-part-save"));
                                        }
                                    }
                                }

                            }

                        } else {
                            sender.sendMessage(MessageUtils.getMessage("players-only"));
                        }

                    } else {
                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                    }

                }

                // #################### \\
                //  EDITOR SECTION END  \\
                // #################### \\

                //SHOW
                else if(args[0].equalsIgnoreCase("show")) {

                    BetterHud.ConsoleError = false;

                    if (args.length > 1) {

                        if (Hud.getByName(args[1]) != null || args[1].equalsIgnoreCase("auto")) {

                            if(!Hud.getByName(args[1]).canBeToggled()) {
                                sender.sendMessage(MessageUtils.getMessage("toggle-display-error"));
                                return true;
                            }

                            Player target;
                            //TARGET = args[2]/all
                            if (args.length > 2) {

                                //TARGET = ALL
                                if (args[2].equalsIgnoreCase("all")) {

                                    if (!sender.hasPermission("betterhud.command.show.all")) {
                                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                                        return true;
                                    }

                                    for (Player targetPlayer : Bukkit.getOnlinePlayers()) {

                                        new Display(targetPlayer, args[1]);
                                    }

                                    if (BetterHud.ConsoleError) {
                                        sender.sendMessage(MessageUtils.getMessage("show-error"));
                                    } else {
                                        sender.sendMessage(MessageUtils.getMessage("show-all").replace("{hudName}", args[1]));
                                    }
                                    return true;

                                }
                                //TARGET = args[2]
                                else {

                                    if (!sender.hasPermission("betterhud.command.show.others")) {
                                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                                        return true;
                                    }

                                    target = Bukkit.getPlayerExact(args[2]);
                                    if(target == null) {
                                        sender.sendMessage(MessageUtils.getMessage("player-offline"));
                                        return true;
                                    }

                                }

                            }
                            //TARGET = SENDER
                            else {

                                if (sender instanceof Player) {
                                    if (!sender.hasPermission("betterhud.command.show")) {
                                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                                        return true;
                                    }

                                    target = (Player) sender;
                                } else {
                                    sender.sendMessage(MessageUtils.getMessage("players-only"));
                                    return true;
                                }

                            }

                            if (target != null) {

                                new Display(target, args[1]);

                                if (BetterHud.ConsoleError) {
                                    sender.sendMessage(MessageUtils.getMessage("show-error"));
                                } else {
                                    sender.sendMessage(MessageUtils.getMessage("show-player").replace("{hudName}", args[1]).replace("{player}", target.getName()));
                                }

                            }
                        } else {
                            sender.sendMessage(MessageUtils.getMessage("hud-not-exists"));
                        }
                    } else {
                        sender.sendMessage(MessageUtils.getMessage("show-no-hud"));
                    }
                }

                //HIDE
                else if(args[0].equalsIgnoreCase("hide")) {

                    BetterHud.ConsoleError = false;

                    Player target;
                    //TARGET = all/args[1]
                    if (args.length > 1) {

                        //TARGET = ALL
                        if(args[1].equalsIgnoreCase("all")) {

                            if (!sender.hasPermission("betterhud.command.hide.all")) {
                                sender.sendMessage(MessageUtils.getMessage("no-permission"));
                                return true;
                            }

                            for(Player targetPlayer : Bukkit.getOnlinePlayers()) {

                                if(Display.getByPlayer(targetPlayer) != null) {
                                    if(Display.getByPlayer(targetPlayer).getActiveHud() != null) {
                                        Display.getByPlayer(targetPlayer).hide();
                                    }
                                }
                            }

                            if(BetterHud.ConsoleError) {
                                sender.sendMessage(MessageUtils.getMessage("hide-error"));
                            } else {
                                sender.sendMessage(MessageUtils.getMessage("hide-all"));
                            }

                            return true;

                        }
                        //TARGET = args[1]
                        else {

                            if (!sender.hasPermission("betterhud.command.hide.other")) {
                                sender.sendMessage(MessageUtils.getMessage("no-permission"));
                                return true;
                            }

                            target = Bukkit.getPlayerExact(args[1]);
                            if(target == null) {
                                sender.sendMessage(MessageUtils.getMessage("player-offline"));
                                return true;
                            }

                        }

                    }
                    //TARGET = SENDER
                    else {

                        if (!sender.hasPermission("betterhud.command.hide")) {
                            sender.sendMessage(MessageUtils.getMessage("no-permission"));
                            return true;
                        }

                        if(sender instanceof Player) {
                            target = (Player) sender;
                        } else {
                            sender.sendMessage(MessageUtils.getMessage("players-only"));
                            return true;
                        }

                    }

                    if(Display.getByPlayer(target) != null) {

                        if(Display.getByPlayer(target).getActiveHud() != null) {

                            Hud hud = Display.getByPlayer(target).getActiveHud();
                            Display.getByPlayer(target).hide();
                            if(BetterHud.ConsoleError) {
                                sender.sendMessage(MessageUtils.getMessage("hide-error"));
                            } else {
                                sender.sendMessage(MessageUtils.getMessage("hide-player").replace("{hudName}", hud.getName()).replace("{player}", target.getName()));
                            }

                        } else {
                            sender.sendMessage(MessageUtils.getMessage("hide-no-active-hud"));
                        }
                    } else {
                        sender.sendMessage(MessageUtils.getMessage("hide-no-active-hud"));
                    }
                }

                //RELOAD
                else if(args[0].equalsIgnoreCase("reload")) {

                    if(sender.hasPermission("betterhud.command.reload")) {

                        BetterHud.ConsoleError = false;
                        if(BetterHud.isPAPILoaded()) {
                            if(BetterHud.expansion.isRegistered())
                                BetterHud.expansion.unregister();
                        }
                        DisplayRunnable.cancelTask();

                        if(args.length > 1) {

                            //ALL
                            if(args[1].equalsIgnoreCase("all")) {
                                ReloadCommand.reloadAll(sender);
                            }
                            //CONFIG
                            else if(args[1].equalsIgnoreCase("config")) {
                                ReloadCommand.reloadConfigs(sender);
                            }
                            //IA
                            else if(args[1].equalsIgnoreCase("itemsadder")) {
                                ReloadCommand.reloadIA(sender);
                            }

                        } else {

                            //ALL
                            ReloadCommand.reloadAll(sender);

                        }

                    } else {
                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                    }

                }

                //EXPORT TEXTURES
                else if(args[0].equalsIgnoreCase("exportTextures")) {

                    if(sender.hasPermission("betterhud.command.exporttextures")) {

                        BetterHud.ConsoleError = false;

                        if(BetterHud.isIASelfHosted()) {
                            sender.sendMessage(MessageUtils.colorize(" &8» &aExporting textures.."));
                            FileUtils.exportTextures("ItemsAdder");
                        } else {
                            sender.sendMessage(MessageUtils.colorize(" &8» &aExporting textures.."));
                            FileUtils.exportTextures("BetterHud");

                            sender.sendMessage(MessageUtils.colorize(" &8» &aCreating new archive inside BetterHud folder.."));
                            FileUtils.zipTextures();
                        }

                        if(BetterHud.ConsoleError) {
                            sender.sendMessage(MessageUtils.colorize(" &8» &cExporting textures failed! Please, check console for more information."));
                        } else {
                            sender.sendMessage(MessageUtils.colorize(" &8» &aEverything done! Please, run &2/bh reload"));
                        }
                    } else {
                        sender.sendMessage(MessageUtils.getMessage("no-permission"));
                    }

                }

                //HELP
                else if(args[0].equalsIgnoreCase("help")) {

                    sender.sendMessage(MessageUtils.colorize("&6&lBetterHud &f&lv"+ BetterHud.getVersion() + " &7&o(( By ApiGames ))"));
                    sender.sendMessage(" ");
                    sender.sendMessage(MessageUtils.colorize("&8- &e/bh show (hud) (player/all) &7- Show the hud for the player"));
                    sender.sendMessage(MessageUtils.colorize("&8- &e/bh hide (player/all) &7- Hide the hud from the player"));
                    sender.sendMessage(MessageUtils.colorize("&8- &e/bh editor &7- Toggle editor mode"));
                    sender.sendMessage(MessageUtils.colorize("&8- &e/bh exportTextures &7- Export BetterHud textures"));
                    sender.sendMessage(MessageUtils.colorize("&8- &e/bh reload (config/itemsadder/all) &7- Reload plugin"));
                    sender.sendMessage(MessageUtils.colorize("&8- &e/bh report-bug &7- Generate report log"));
                    sender.sendMessage(" ");

                }
                else {
                    sender.sendMessage(MessageUtils.getMessage("unknown-sub-command"));
                }

            } else {
                Bukkit.dispatchCommand(sender, "bh help");
            }
        }

        return true;
    }
}
