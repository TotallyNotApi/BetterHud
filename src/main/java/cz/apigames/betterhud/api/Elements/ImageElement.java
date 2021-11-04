package cz.apigames.betterhud.api.Elements;

import cz.apigames.betterhud.api.Utils.Condition;
import cz.apigames.betterhud.api.Utils.MessageUtils;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import org.bukkit.entity.Player;

public class ImageElement extends Element {

    protected int width = 0;

    /**
     * Constructs an ImageElement with the given path to the texture
     *
     * @param name the internal name of this element used in config
     * @param x the x coordinate in pixels (0-1910)
     * @param y the y coordinate
     * @param scale the scale of this element
     * @param imagePath the path to the texture file in format: "%namespace%:%image_name%"
     */
    public ImageElement(String name, int x, int y, int scale, String imagePath) {
        super(name, x, y, scale);

        config_name = "IMAGE";

        super.value = imagePath;

        FontImageWrapper image = new FontImageWrapper(getImageName() + "-" + y + "_" + scale);
        if(image.exists()) {

            width = image.getWidth();

        }
    }

    /* --- SETTERS --- */

    /**
     * Change the width of the image
     * (Note: This wont change the rendered image!! This "width" value is for internal calculations only!)
     *
     * @param width the width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /* --- GETTERS --- */

    /**
     * @return the name internal of the image in format: "%namespace%:%image_name%"
     */
    public String getImageName() {
        String[] fileName = value.split("/");
        return value.split(":")[0] + ":" + fileName[fileName.length-1].split("\\.")[0];
    }

    /**
     * @return the path to the image"
     */
    public String getImagePath() {
        return super.value.split(":")[1];
    }

    /**
     * @return the namespace id
     */
    public String getNamespace() {
        return super.value.split(":")[0];
    }

    /* --- METHODS --- */

    @Override
    public int calculateWidth(Player player) {
        return width;
    }

    @Override
    public String getFor(Player player, String value) {

        if(!isVisible(player) || !Condition.checkFor(player, conditions)) {
            return "";
        }

        FontImageWrapper image = new FontImageWrapper(getImageName() + "-" + y + "_" + scale);
        if(image.exists()) {

            return image.applyPixelsOffset(ix) + MessageUtils.colorize("&r");

        }
        return "";
    }

}
