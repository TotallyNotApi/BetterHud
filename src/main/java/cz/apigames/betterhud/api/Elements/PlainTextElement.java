package cz.apigames.betterhud.api.Elements;

import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Utils.Condition;
import cz.apigames.betterhud.api.Utils.MessageUtils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.entity.Player;

public class PlainTextElement extends Element {

    /**
     * Constructs an PlainTextElement with the given value
     *
     * @param name the internal name of this element used in config
     * @param value the String value you want to be displayed
     */
    public PlainTextElement(String name, int x, String value) {
        super(name, x, 0, 0);
        this.value = value;

        config_name = "PLAIN_TEXT";
    }

    /* --- METHODS --- */

    @Override
    public String getFor(Player player, String value) {

        if(!isVisible(player) || !Condition.checkFor(player, conditions)) {
            return "";
        }

        return FontImageWrapper.applyPixelsOffsetToString(MessageUtils.colorize(MessageUtils.translatePlaceholders(value + "&r", player, BetterHudAPI.getPlaceholders(player))), ix);

    }

    @Override
    public int calculateWidth(Player player) {
        return MessageUtils.getRawMessage(value).length()*4;
    }

}
