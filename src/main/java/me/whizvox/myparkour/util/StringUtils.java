package me.whizvox.myparkour.util;

public class StringUtils {

    public static String formatTime(int ticks) {
        int subTicks = ticks % 20;
        int seconds = (ticks / 20) % 60;
        int minutes = ticks / 1200;
        return String.format("%d:%02d.%02d", minutes, seconds, subTicks);
    }

}
