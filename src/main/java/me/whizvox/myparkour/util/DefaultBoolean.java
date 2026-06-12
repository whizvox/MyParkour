package me.whizvox.myparkour.util;

public enum DefaultBoolean {

    DEFAULT,
    FALSE,
    TRUE;

    public final String repr;

    DefaultBoolean() {
        this.repr = toString().toLowerCase();
    }

    public static DefaultBoolean parse(String value) {
        return switch (value) {
            case "default" -> DEFAULT;
            case "true" -> TRUE;
            case "false" -> FALSE;
            default -> throw new IllegalArgumentException("Invalid defaultable boolean value: " + value);
        };
    }

}
