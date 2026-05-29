package me.whizvox.myparkour.course;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.myparkour.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public enum CourseFlag {

    FAIL_WATER,
    FAIL_LAVA,
    FALL_DAMAGE;

    private final String name;

    CourseFlag() {
        name = StringUtils.snakeToLowerCamelCase(name());
    }

    @Override
    public String toString() {
        return name;
    }

    public static final Map<String, CourseFlag> NAMES;

    static {
        Map<String, CourseFlag> names = new Object2ObjectOpenHashMap<>();
        for (CourseFlag flag : CourseFlag.values()) {
            names.put(flag.name, flag);
        }
        NAMES = Collections.unmodifiableMap(names);
    }

    public static Optional<CourseFlag> parse(String name) {
        return Optional.ofNullable(NAMES.get(name));
    }

}
