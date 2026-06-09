package me.whizvox.myparkour.util;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@NotNullByDefault
public abstract class DefaultConfiguration {

    private @Nullable Configuration oldConfig;
    private @Nullable FileConfiguration newConfig;

    public DefaultConfiguration() {
        oldConfig = null;
        newConfig = null;
    }

    public void reset(Configuration config) {
        oldConfig = config;
        newConfig = new YamlConfiguration();
    }

    protected final void setComments(String path, String... comments) {
        if (newConfig != null) {
            newConfig.setComments(path, List.of(comments));
        }
    }

    protected final void set(String path, Object value, String... comments) {
        if (oldConfig != null && newConfig != null) {
            if (oldConfig.contains(path)) {
                newConfig.set(path, oldConfig.get(path));
            } else {
                newConfig.set(path, value);
            }
            newConfig.setComments(path, List.of(comments));
        }
    }

    public FileConfiguration getConfiguration() {
        return Objects.requireNonNull(newConfig);
    }

    public abstract void generateDefaults();

}
