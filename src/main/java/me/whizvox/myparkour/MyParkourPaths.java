package me.whizvox.myparkour;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record MyParkourPaths(Path pluginDir, Path dbFile, Path messagesFile, Path coursesFile, Path signsFile, Path editsFile, Path runsPath) {

    public MyParkourPaths(Path pluginDir) {
        this(pluginDir, pluginDir.resolve("myparkour.db"), pluginDir.resolve("messages.json"), pluginDir.resolve("courses.json"), pluginDir.resolve("signs.json"), pluginDir.resolve("edits.json"), pluginDir.resolve("runs.yml"));
    }

    public void mkdir() {
        try {
            Files.createDirectories(pluginDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
