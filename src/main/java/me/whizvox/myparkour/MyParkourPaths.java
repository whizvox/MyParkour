package me.whizvox.myparkour;

import java.nio.file.Path;

public record MyParkourPaths(Path pluginDir, Path messagesFile, Path coursesFile, Path timesFile, Path editsFile) {

    public MyParkourPaths(Path pluginDir) {
        this(pluginDir, pluginDir.resolve("messages.json"), pluginDir.resolve("courses.json"), pluginDir.resolve("times.json"), pluginDir.resolve("edits.json"));
    }

}
