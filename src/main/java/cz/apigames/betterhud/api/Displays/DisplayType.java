package cz.apigames.betterhud.api.Displays;

public enum DisplayType {

    ACTIONBAR,
    BOSSBAR,
    CHAT;

    public static DisplayType getDisplayType(Display display) {

        if(display instanceof ActionBarDisplay) {
            return ACTIONBAR;
        } else if(display instanceof BossBarDisplay) {
            return BOSSBAR;
        } else {
            return null;
        }

    }

}
