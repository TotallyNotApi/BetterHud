package cz.apigames.betterhud.api.Utils;

import cz.apigames.betterhud.api.Displays.DisplayType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ToggleEvent {

    @NotNull(message = "EventType is not valid or is null.")
    protected EventType eventType;

    @NotNull(message = "Display is not valid or is null.")
    protected DisplayType displayType;

    protected String opt_value;

    @Min(message = "Value 'hide_after' must be a positive number", value = 0)
    protected int hide_after;

    /**
     * Constructs a ToggleEvent with the given eventType and displayType
     *
     * @param eventType the type of the event
     * @param displayType the display type which will be used for displaying hud
     * @param hide_after time in seconds when the hud will disappear (0 = never)
     *
     * @see EventType the list of valid event types
     */
    public ToggleEvent(EventType eventType, DisplayType displayType, int hide_after) {

        this.eventType = eventType;
        this.displayType = displayType;
        this.hide_after = hide_after;

    }

    /**
     * Constructs a ToggleEvent with the given eventType and displayType
     *
     * @param eventType the type of the event
     * @param displayType the display type which will be used for displaying hud
     * @param value optional value that is needed for event types: COMMAND, GAMEMODE_CHANGE
     * @param hide_after time in seconds when the hud will disappear (0 = never)
     *
     * @see EventType the list of valid event types
     */
    public ToggleEvent(EventType eventType, DisplayType displayType, String value, int hide_after) {

        this.eventType = eventType;
        this.displayType = displayType;
        this.hide_after = hide_after;
        this.opt_value = value;

    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getOpt_value() {
        return opt_value;
    }

    public int getHideAfter() {
        return hide_after;
    }

    public static enum EventType {

        PLAYER_JOIN,
        COMMAND,
        GAMEMODE_CHANGE

    }
}
