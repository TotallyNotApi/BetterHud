package cz.apigames.betterhud.api.Elements;

public enum Alignment {

    LEFT,
    CENTER,
    RIGHT;

    public static Alignment get(String str) {

        if(str == null) {
            return Alignment.LEFT;
        }

        if(str.equalsIgnoreCase("left")) {
            return Alignment.LEFT;
        } else if(str.equalsIgnoreCase("center")) {
            return Alignment.CENTER;
        } else if(str.equalsIgnoreCase("right")) {
            return Alignment.RIGHT;
        } else {
            return Alignment.LEFT;
        }

    }

}
