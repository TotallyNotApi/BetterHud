package cz.apigames.betterhud.api.Elements;

import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Utils.Condition;
import cz.apigames.betterhud.api.Utils.MessageUtils;
import org.bukkit.entity.Player;

import javax.validation.constraints.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Element {

    protected String config_name = "NULL";

    @NotEmpty(message = "Name of the element cannot be null or empty.")
    protected String name;

    @Max(value = 1910, message = "X-coordinate of the element cannot be greater than 1910")
    @PositiveOrZero(message = "X-coordinate of the element cannot be negative.")
    @NotNull(message = "X-coordinate of the element cannot be null")
    protected int x;

    @NotNull(message = "Y-coordinate of the element cannot be null")
    protected int y;

    @NotNull(message = "Scale of the element cannot be null")
    protected int scale;

    @NotNull(message = "Element value cannot be null")
    protected String value;

    protected Alignment align = Alignment.LEFT;

    protected Set<Player> hidden;
    protected HashMap<Player, String> values;
    protected Set<Condition> conditions = new HashSet<>();

    public int ix, iy; //INTERNAL USE ONLY

    /**
     * Constructs an element at the given coordinates with the given scale
     *
     * @param name the internal name of this element used in config
     * @param x the x coordinate in pixels (0-1910)
     * @param y the y coordinate
     * @param scale the scale of this element
     */
    public Element(String name, int x, int y, int scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.name = name;
        this.hidden = new HashSet<>();
        this.values = new HashMap<>();
    }

    /* --- GETTERS --- */

    /**
     * @return internal name of this element (used in config)
     */
    public String getName() {
        return name;
    }

    /**
     * @return the X coordinate of this element
     */
    public int getX() {
        return x;
    }

    /**
     * @return the Y coordinate of this element
     */
    public int getY() {
        return y;
    }

    /**
     * @return the scale (height) of rendered element
     */
    public int getScale() {
        return scale;
    }

    /**
     * @param player the player
     *
     * @return the boolean if element is visible for specified player
     */
    public boolean isVisible(Player player) {
        return !hidden.contains(player);
    }

    /**
     * Get the default value that is currently displayed via this element
     *
     * @return value of this element
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the value that is currently displayed via this element for specified player
     *
     * @return the value
     */
    public String getValue(Player player) {
        return values.getOrDefault(player, value);
    }

    /**
     * Get the alignment of this element
     *
     * @return the current alignment
     */
    public Alignment getAlign() {
        return align;
    }

    /* --- SETTERS --- */

    /**
     * Sets the internal name of this element
     *
     * @param name   the new name of the element
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the X coordinate of this element (in pixels 0-1910)
     *
     * @param x   the X coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Subtract the value from X coordinate of this element
     *
     * @param x   the decrement
     */
    public void subtractX(int x) { this.x = this.x-x; }

    /**
     * Add the value to X coordinate of this element
     *
     * @param x   the increment
     */
    public void addX(int x) { this.x = this.x+x; }

    /**
     * Sets the Y coordinate of this element
     *
     * @param y   the Y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets the scale (height) of this element
     *
     * @param scale   the scale (height) of rendered element
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * Changes the value that is currently displayed via this element (for all players)
     *
     * @param value String that you want to display
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Changes the value that is currently displayed via this element for specified player
     *
     * @param player the player
     * @param value String that you want to display
     */
    public void setValue(Player player, String value) {
        values.put(player, value);
    }

    /**
     * Change the visibility of this element for specified player
     *
     * @param player the player
     * @param visible boolean value
     */
    public void setVisibility(Player player, boolean visible) {
        if(visible) {
            this.hidden.remove(player);
        } else {
            this.hidden.add(player);
        }
    }

    /**
     * Change the alignment of this element. Default: LEFT
     *
     * @param align the alignment (availabne: LEFT, CENTER, RIGHT)
     */
    public void setAlign(Alignment align) {
        this.align = align;
    }

    /* --- METHODS --- */

    /**
     * Adds the specified condition to this element
     *
     * @param condition   the condition
     */
    public void addCondition(Condition condition) {
        conditions.add(condition);
    }

    /**
     * Resets the per-player value
     * @see #setValue(Player, String)
     *
     * @param player the player
     */
    public void resetValue(Player player) {
        values.remove(player);
    }

    /**
     * Resets all active per-player values
     * @see #setValue(Player, String)
     */
    public void resetAllValues() {
        values.clear();
    }

    /**
     * @return the rendered element for specified player
     *
     * @param player the player
     */
    public String getFor(Player player) {
        if(values.containsKey(player)) {
            return getFor(player, values.get(player));
        } else {
            return getFor(player, value);
        }
    }

    /**
     * @return the rendered element for specified player with custom value
     *
     * @param player the player
     * @param value the String value that should be displayed
     */
    public abstract String getFor(Player player, String value);

    /**
     * @return String that is used in configurations -> config.yml, characters.yml
     */
    public String getConfigName() {
        return config_name;
    }

    public int calculateWidth(Player player) {

        int width = 0;

        String message = MessageUtils.stripColors(getValue(player));
        message = MessageUtils.translatePlaceholders(message, player);

        for(Character ch : message.toCharArray()) {
            String charName = Character.isSpaceChar(ch) ? "blank" : BetterHudAPI.charactersInternalNames.get(ch);
            if(BetterHudAPI.fontImageCharacters.containsKey(charName + "-" + y + "_" + scale)) {
                width += BetterHudAPI.fontImageCharacters.get(charName + "-" + y + "_" + scale).getWidth()/2;
            }
        }
        return width;

    }

}
