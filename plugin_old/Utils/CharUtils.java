package cz.apigames.betterhud.plugin_old.Utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CharUtils {

    private static final Pattern legacyPattern = Pattern.compile("&[0-9a-fk-or]");
    private static final Pattern hexPattern = Pattern.compile("(?<!\\\\)(\\{#[a-fA-F0-9]{6}})");

    public static List<Character> convertStringToCharList(String str) {
        List<Character> chars = str.chars().mapToObj(e -> (char)e).collect(Collectors.toList());
        return chars;
    }

    public static boolean isLegacyColorCode(List<Character> chars, int index) {
        String colorCode = chars.get(index).toString() + chars.get(index+1).toString();
        Matcher matcher = legacyPattern.matcher(colorCode);
        return matcher.find();
    }

    public static boolean isHexColorCode(List<Character> chars, int index) {
        StringBuilder builder = new StringBuilder();
        for(int i=index;i<index+9;i++) {
            builder.append(chars.get(i));
        }

        Matcher matcher = hexPattern.matcher(builder.toString());
        return matcher.find();
    }

    public static String removeLastChars(String message, int chars) {
        return message.substring(0, message.length() - chars);
    }

}
