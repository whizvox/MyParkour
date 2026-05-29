package me.whizvox.myparkour.util;

public class StringUtils {

    public static String formatTime(int ticks) {
        int subTicks = ticks % 20;
        int seconds = (ticks / 20) % 60;
        int minutes = ticks / 1200;
        return String.format("%d:%02d.%02d", minutes, seconds, subTicks * 5);
    }

    public static String snakeToLowerCamelCase(String str) {
        String[] parts = str.split("_");
        StringBuilder sb = new StringBuilder();
        sb.append(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

}
