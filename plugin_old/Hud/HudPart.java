package cz.apigames.betterhud.plugin_old.Hud;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Configs.ConfigManager;
import cz.apigames.betterhud.plugin_old.Hud.Editor.EditMode;
import cz.apigames.betterhud.plugin_old.Utils.CharUtils;
import cz.apigames.betterhud.plugin_old.Utils.MessageUtils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HudPart {

    public enum Type {

        ICON,
        INTEGER,
        TEXT

    }

    private final String hudName;
    private final String partName;
    private String value;
    private Type type;
    private int positionX;
    private int positionY;
    private int scale;
    private int max;
    private boolean initialized = false;

    public HudPart(String hudName, String partName) {

        this.hudName = hudName;
        this.partName = partName;

        YamlConfiguration config = ConfigManager.getConfig("config.yml");

        try {
            type = Type.valueOf(config.getString("configuration.hud-content."+hudName+"."+partName+".type"));
        } catch (IllegalArgumentException e) {
            BetterHud.error("&cFailed to load &4"+hudName+" &chud! &4"+partName+"'s &ctype is not valid!", e);
            return;
        } catch (NullPointerException e) {
            BetterHud.error("&cFailed to load &4"+hudName+" &chud! &4"+partName+"'s &ctype is not set!", e);
            return;
        }

        positionX = config.getInt("configuration.hud-content."+hudName+"."+partName+".position-x");
        positionY = config.getInt("configuration.hud-content."+hudName+"."+partName+".position-y");
        scale = config.getInt("configuration.hud-content."+hudName+"."+partName+".scale");

        if(positionY > scale) {
            positionY = scale;
            BetterHud.sendErrorToConsole("&cFailed to load &4"+hudName+" &chud! &4"+partName+"'s &cposition-y must be lower than or equal to the scale! Position was set to &4"+scale);
        }

        if(type.equals(Type.ICON)) {
            value = config.getString("configuration.hud-content."+hudName+"."+partName+".texture-path");
        }
        else if(type.equals(Type.INTEGER) || type.equals(Type.TEXT)) {
            value = config.getString("configuration.hud-content."+hudName+"."+partName+".input");
            if(config.isSet("configuration.hud-content."+hudName+"."+partName+".max-length")) {
                max = config.getInt("configuration.hud-content."+hudName+"."+partName+".max-length");
            } else {
                BetterHud.sendErrorToConsole("&cFailed to load &4"+hudName+" &chud! &4"+partName+"'s &cmax-length is not set!");
                return;
            }
        }
        else {
            BetterHud.sendErrorToConsole("&cFailed to load &4"+hudName+" &chud! &4"+partName+"'s &ctype is not valid!");
            return;
        }
        if(value == null) {
            BetterHud.sendErrorToConsole("&cFailed to load &4"+hudName+" &chud! Part &4"+partName+" &cdoesn't have valid input/texture! Please, check your configuration!");
            return;
        }

        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Type getType() {
        return type;
    }

    public String getPartName() {
        return partName;
    }

    public String getValue() {
        return value;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getPositionX() { return positionX; }

    public int getScale() {
        return scale;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    private void disable(Player player) {

        initialized = false;
        player.sendMessage(MessageUtils.getMessage("hud-display-error"));

    }

    public String get(Player player) {

        if(!initialized) {
            return "";
        }

        BetterHud.ConsoleError = false;

        //MESSAGE SETUP
        String message = value;
        message = MessageUtils.translatePlaceholders(message,player);

        //EDIT MODE
        boolean editing = false;
        if(EditMode.getByPlayer(player) != null && !type.equals(Type.ICON)) {

            EditMode mode = EditMode.getByPlayer(player);

            if(mode.getEditingHudName().equals(hudName)) {

                if(mode.getEditingPartName().equals(partName)) {

                    editing = true;

                    if(ConfigManager.getConfig("config.yml").isSet("configuration.hud-content."+hudName+"."+partName+".fixed-position")) {
                        if (ConfigManager.getConfig("config.yml").getBoolean("configuration.hud-content." + hudName + "." + partName + ".fixed-position")) {

                            message = "";
                            //EDITOR
                            for(int i=0; i<max; i++) {
                                message = message+"0";
                            }

                        }
                    }
                }
            }
        }

        Path partsFile = Paths.get("plugins/ItemsAdder/data/items_packs/betterhud/hud_parts.yml");
        YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(partsFile.toFile());

        String output = "";

        //ICONS
        if(type.equals(Type.ICON)) {

            String namespaceID = hudName+"-"+partName;

            if(yamlFile.isSet("font_images."+namespaceID+".path")) {

                FontImageWrapper font = new FontImageWrapper("betterhud:"+namespaceID);
                if(font.exists()) {
                    output = font.applyPixelsOffset(positionX);
                } else {
                    BetterHud.sendErrorToConsole("&cIcon &4'"+value+"&c' is not loaded in hud &4"+hudName+"&c, part name: &4"+partName+"&c! Try &4/bh reload");
                    disable(player);
                    return "";
                }

            } else {
                BetterHud.sendErrorToConsole("&cCan't find icon &4'"+value+"&c' in hud &4"+hudName+"&c, part name: &4"+partName);
                disable(player);
                return "";
            }

        }

        //OTHER TYPES
        else {

            StringBuilder stringBuilder = new StringBuilder();

            //POSITIONING
            if(message.length() < max) {

                if(ConfigManager.getConfig("config.yml").isSet("configuration.hud-content."+hudName+"."+partName+".fixed-position")) {
                    if(ConfigManager.getConfig("config.yml").getBoolean("configuration.hud-content."+hudName+"."+partName+".fixed-position")) {

                        String align = "left";
                        if(ConfigManager.getConfig("config.yml").isSet("configuration.hud-content."+hudName+"."+partName+".align")) {
                            align = ConfigManager.getConfig("config.yml").getString("configuration.hud-content."+hudName+"."+partName+".align");
                        }

                        if(!align.equalsIgnoreCase("right") && !align.equalsIgnoreCase("left")) {
                            BetterHud.sendErrorToConsole("Invalid align type for hud part &4"+partName+"&c!");
                            disable(player);
                            return "";
                        }

                        StringBuilder spaces = new StringBuilder();

                        for(int i=message.length(); i<=max; i++) {
                            spaces.append(" ");
                        }

                        if(align.equalsIgnoreCase("right")) {

                            message = spaces.toString()+message;

                        } else if(align.equalsIgnoreCase("left")) {

                            message = message+spaces.toString();

                        }

                    }
                }

            }

            List<Character> chars = CharUtils.convertStringToCharList(message);

            //CHAR LOOP
            int spacing = 0;
            int charCount = 0;
            for(int i=0;i<chars.size();i++) {

                Character ch = chars.get(i);

                //COLOR HANDLER
                if(ch.toString().equalsIgnoreCase("&")) {
                    if(CharUtils.isLegacyColorCode(chars, i)) {
                        stringBuilder.append(ch).append(chars.get(i+1));
                        i += 1;
                        continue;
                    }
                } else if(ch.toString().equalsIgnoreCase("{")) {
                    if(CharUtils.isHexColorCode(chars, i)) {

                        for(int i2=0; i2<9; i2++) {
                            stringBuilder.append(chars.get(i+i2));
                        }

                        i += 8;
                        continue;
                    }
                }

                //AFTER COLOR CHECK - CHAR++
                if(charCount++ >= max) {
                    break;
                }

                //NAMESPACE
                String namespaceID;
                if(Character.isUpperCase(ch)) {
                    namespaceID = hudName+"-"+partName+"-"+ch.toString().toLowerCase()+"-big";
                }
                else if(Character.isSpaceChar(ch)) {
                    namespaceID = hudName+"-"+partName+"-blank";
                }
                else {
                    if(ch.toString().equalsIgnoreCase(".")) {
                        namespaceID = hudName+"-"+partName+"-"+"dot";
                    } else {
                        namespaceID = hudName+"-"+partName+"-"+ch.toString().toLowerCase();
                    }
                }

                if (yamlFile.isSet("font_images." + namespaceID + ".path")) {

                    FontImageWrapper font = new FontImageWrapper("betterhud:" + namespaceID);
                    if (font.exists()) {
                        stringBuilder.append(font.applyPixelsOffset(positionX+spacing));
                    } else {
                        BetterHud.sendErrorToConsole("&cCharacter &4'" + ch + "&c' is not loaded in hud &4" + hudName + "&c, part name: &4" + partName + "&c! Try &4/bh reload");
                        disable(player);
                        return "";
                    }

                } else {
                    BetterHud.sendErrorToConsole("&cCan't find character &4'" + ch + "&c' in hud &4" + hudName + "&c, part name: &4" + partName);
                    disable(player);
                    return "";
                }

                spacing++;
            }

            output = editing ? "&a"+stringBuilder.toString()+"&r" : stringBuilder.toString();

        }

        initialized = !BetterHud.ConsoleError;

        return output;
    }

    public void save() {

        File configFile = new File(BetterHud.getPlugin().getDataFolder(), "config.yml");
        YamlConfiguration yamlFile = YamlConfiguration.loadConfiguration(configFile);

        yamlFile.set("configuration.hud-content."+hudName+"."+partName+".position-x", positionX);

        try{
            yamlFile.save(configFile);
        } catch (IOException e) {
            BetterHud.error("&cAn error occurred while saving config file! Please, restart server and try it again!", e);
        }

    }


}
