package cz.apigames.betterhud.api.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

public class Condition {

    @NotEmpty(message = "Condition's value can't be null or empty!")
    protected String condition;

    public Condition(String condition) {
        this.condition = condition;
    }

    public static boolean checkFor(Player player, Set<Condition> conditionSet) throws NumberFormatException {

        for(Condition cond : conditionSet) {

            if(!cond.checkFor(player)) {
                return false;
            }

        }
        return true;
    }

    public boolean checkFor(Player player) throws NumberFormatException {

        //PERMISSION
        if(condition.contains("perm=")) {
            return player.hasPermission(condition.split("perm=")[1]);
        } else if(condition.contains("compare=")) {

            String compare = condition.split("compare=")[1];
            try {
                if(compare.contains(">")) {

                    int value1 = Integer.parseInt(MessageUtils.translatePlaceholders(compare.split(">")[0], player));
                    int value2 = Integer.parseInt(MessageUtils.translatePlaceholders(compare.split(">")[1], player));

                    return value1>value2;

                } else if(compare.contains("<")) {

                    int value1 = Integer.parseInt(MessageUtils.translatePlaceholders(compare.split("<")[0], player));
                    int value2 = Integer.parseInt(MessageUtils.translatePlaceholders(compare.split("<")[1], player));

                    return value1<value2;

                } else if(compare.contains("<=")) {

                    int value1 = Integer.parseInt(MessageUtils.translatePlaceholders(compare.split("<=")[0], player));
                    int value2 = Integer.parseInt(MessageUtils.translatePlaceholders(compare.split("<=")[1], player));

                    return value1<=value2;

                } else if(compare.contains(">=")) {
                    int value1 = Integer.parseInt(MessageUtils.translatePlaceholders(compare.split(">=")[0], player));
                    int value2 = Integer.parseInt(MessageUtils.translatePlaceholders(compare.split(">=")[1], player));

                    return value1>=value2;
                } else if(compare.contains("==")) {
                    String value1 = MessageUtils.translatePlaceholders(compare.split("==")[0], player);
                    String value2 = MessageUtils.translatePlaceholders(compare.split("==")[1], player);

                    return value1.equalsIgnoreCase(value2);
                } else if(compare.contains("===")) {
                    String value1 = MessageUtils.translatePlaceholders(compare.split("===")[0], player);
                    String value2 = MessageUtils.translatePlaceholders(compare.split("===")[1], player);

                    return value1.equals(value2);
                }
            } catch (NumberFormatException ignored) {
                throw new NumberFormatException("Can't parse this expression '"+compare+"', because it doesn't contain numbers.");
            }

        }
        return false;

    }

}
