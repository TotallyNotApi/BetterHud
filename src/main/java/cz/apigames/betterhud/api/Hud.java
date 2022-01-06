package cz.apigames.betterhud.api;

import cz.apigames.betterhud.api.Displays.ActionBarDisplay;
import cz.apigames.betterhud.api.Displays.BossBarDisplay;
import cz.apigames.betterhud.api.Displays.Display;
import cz.apigames.betterhud.api.Displays.DisplayType;
import cz.apigames.betterhud.api.Elements.Alignment;
import cz.apigames.betterhud.api.Elements.Element;
import cz.apigames.betterhud.api.Utils.Condition;
import cz.apigames.betterhud.api.Utils.Placeholder;
import cz.apigames.betterhud.api.Utils.ToggleEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.*;

public class Hud {

    @NotEmpty(message = "Name of the hud cannot be empty or null")
    protected String name;

    protected List<Element> elements = new ArrayList<>();

    @Min(value = 100, message = "Refresh interval of the hud should not be less than 100")
    protected Integer refreshInterval = 1000;

    protected Set<Condition> conditions = new HashSet<>();
    protected Set<ToggleEvent> events = new HashSet<>();

    /* --- GETTERS --- */

    /**
     * Returns element of this hud by its internal name
     *
     * @param elementName internal name of the element
     * @return optional Element instance
     */
    public Optional<Element> getElement(String elementName) {
        return elements.stream().filter(element -> element.getName().equalsIgnoreCase(elementName)).findFirst();
    }

    /**
     * Returns loaded events of this hud
     *
     * @return the list of ToggleEvents
     */
    public Set<ToggleEvent> getEvents() {
        return events;
    }

    /**
     * Returns elements of this hud
     *
     * @return the list of hud elements
     */
    public List<Element> getElements() {
        return elements;
    }

    /**
     * Returns refresh interval for this hud
     *
     * @return the refresh interval in milliseconds
     */
    public int getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * Check if the hud is visible for specified player
     *
     * @param player the player
     * @return true if hud is visible for player
     */
    public boolean isVisible(Player player) {
        return !Display.getDisplays(player, this).isEmpty();
    }

    /**
     * Returns internal name of the hud
     *
     * @return the name of the hud (used in config)
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the state if hud contains conditions
     *
     * @return the boolean, true if hud has conditions
     */
    public boolean hasConditions() {
        return !conditions.isEmpty();
    }

    /* --- SETTERS --- */

    /**
     * Sets the internal name of this hud
     *
     * @param name internal name of the hud
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the refresh interval for this hud
     *
     * @param refreshInterval the interval in milliseconds
     */
    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
    /* --- METHODS --- */

    /**
     * Adds the specified element to this hud.
     *
     * @param t   the element to add to this hud
     * @param <T> the element's type
     * @return added element
     */
    public <T extends Element> T addElement(@NotNull T t)
    {

        Objects.requireNonNull(t);
        elements.add(t);
        elements.sort(Comparator.comparingInt(Element::getX));
        return t;

    }

    /**
     * Adds the specified list of elements to this hud.
     *
     * @param elements   the list of elements to add to this hud
     */
    public void addElements(@NotNull List<Element> elements)
    {

        Objects.requireNonNull(elements);
        this.elements.addAll(elements);
        this.elements.sort(Comparator.comparingInt(Element::getX));

    }

    /**
     * Adds the specified condition to this hud
     *
     * @param condition   the condition
     */
    public void addCondition(Condition condition) {
        conditions.add(condition);
    }

    /**
     * Adds the specified ToggleEvent which will toggle this hud
     *
     * @param toggleEvent   the new instance of ToggleEvent
     */
    public void addEvent(ToggleEvent toggleEvent) {
        events.add(toggleEvent);
    }

    /**
     * Removes the specified element from this hud.
     *
     * @param element   the element to remove from this hud
     * @return true if this hud contained the specified element
     */
    public boolean removeElement(@NotNull Element element) {

        return this.elements.remove(element);

    }

    /**
     * Removes the specified elements from this hud.
     *
     * @param elements   the list of element to remove from this hud
     * @return true if this hud contained the specified elements
     */
    public boolean removeElements(@NotNull List<Element> elements) {

        return this.elements.removeAll(elements);

    }

    /**
     * Renders the hud for specified player
     *
     * @param player   the player
     * @return rendered hud in String
     */
    public String getRenderedString(Player player) {
        calculateOffsets(player);
        StringBuilder builder = new StringBuilder();
        elements.forEach(element -> {

            builder.append(element.getFor(player));

        });
        return builder.toString();
    }

    /**
     * Renders the hud for specified player for specified time
     *
     * @param player   the player
     * @param displayType  where the hud should be rendered (ACTIONBAR, BOSSBAR, CHAT)
     * @param hideAfter time in seconds the hud should disappear
     * @param override when there is some hud already displayed, should we override it?
     *                 When hideAfter is set, the old hud will be re-displayed after that time
     *
     * @throws IllegalArgumentException when displayType is not valid
     * @throws IllegalStateException when some hud is already displayed via specified displayType
     *
     * @return true if player have met all specified conditions
     */
    public boolean renderFor(Player player, DisplayType displayType, int hideAfter, boolean override) throws IllegalStateException, IllegalArgumentException {

        for(Condition cond : conditions) {
            if(!cond.checkFor(player)) return false;
        }

        if(Display.getDisplays(player).stream().anyMatch(display -> DisplayType.getDisplayType(display).equals(displayType))) {

            if(override) {

                Display.getDisplays(player).stream().filter(display -> DisplayType.getDisplayType(display).equals(displayType)).findFirst().ifPresent(display -> {

                    if(hideAfter != 0) {

                        Hud oldHud = display.getHud();
                        display.destroy();

                        Bukkit.getScheduler().runTaskLaterAsynchronously(BetterHudAPI.getPlugin(), () -> {

                            oldHud.renderFor(player, displayType, 0, true);

                        }, hideAfter * 20L);

                    } else {

                        display.destroy();

                    }

                });

            } else {
                throw new IllegalStateException("Some hud is already displayed via this display!");
            }

        }

        if(displayType.equals(DisplayType.CHAT)) {
            player.sendMessage(getRenderedString(player));
            return true;
        } else if(displayType.equals(DisplayType.ACTIONBAR)) {
            new ActionBarDisplay(player, this);
        } else if(displayType.equals(DisplayType.BOSSBAR)) {
            new BossBarDisplay(player, this);
        } else {
            throw new IllegalArgumentException("Invalid DisplayType value!");
        }

        if(hideAfter != 0) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(BetterHudAPI.getPlugin(), () -> {

                if(isVisible(player)) {

                    hide(player);

                }


            }, hideAfter* 20L);
        }
        return true;
    }

    /**
     * Renders the hud for specified player
     *
     * @param player   the player
     * @param displayType  where the hud should be rendered (ACTIONBAR, BOSSBAR, CHAT)
     *
     * @throws IllegalArgumentException when displayType is not valid
     * @throws IllegalStateException when some hud is already displayed via specified displayType
     */
    public boolean renderFor(Player player, DisplayType displayType) throws IllegalStateException, IllegalArgumentException {
        return renderFor(player, displayType, 0, false);
    }

    /**
     * Hides the hud from specified player
     *
     * @param player   the player
     * @return true if hud was visible and hidden successfully
     */
    public boolean hide(Player player) {

        List<Display> displays = Display.getDisplays(player, this);

        if(!displays.isEmpty()) {
            displays.forEach(Display::destroy);
            return true;
        }

        return false;
    }

    /**
     * Calculates offsets for the hud
     *
     * @param player the player
     */
    public void calculateOffsets(Player player) {

        for(int i=0;i<elements.size();i++) {

            Element currElement = elements.get(i);

            if(!currElement.isVisible(player)) {
               continue;
            }

            int plus = 0, minus = 0;

            //MINUS
            for(int x=i-1;x>=0;x--) {
                minus -= elements.get(x).calculateWidth(player);
            }

            //PLUS
            for(int x=i+1;x<elements.size();x++) {
                plus += elements.get(x).calculateWidth(player);
            }

            if(currElement.getAlign().equals(Alignment.LEFT)) {
                currElement.ix = ((elements.get(i).getX() - 955) + plus + minus) + (elements.get(i).calculateWidth(player));
            } else if(currElement.getAlign().equals(Alignment.RIGHT)) {
                currElement.ix = ((elements.get(i).getX() - 955) + plus + minus) - (elements.get(i).calculateWidth(player));
            } else if(currElement.getAlign().equals(Alignment.CENTER)) {
                currElement.ix = ((elements.get(i).getX() - 955) + plus + minus);
            }
            currElement.iy = (elements.get(i).getY());

        }

    }

    /**
     * Generates new name for element
     *
     * @return available name
     */
    public String generateElementName() {

        int i = 0;
        while (true) {

            int finalI = i;
            if(elements.stream().anyMatch(element -> element.getName().equals("generatedElement-"+finalI))) {
                i++;
            } else {
                break;
            }

        }

        return "generatedElement-"+i;

    }

}
