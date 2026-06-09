package me.whizvox.myparkour.core;

import me.whizvox.myparkour.course.ExitGameMode;
import me.whizvox.myparkour.course.StartGameMode;
import me.whizvox.myparkour.util.DefaultConfiguration;

import java.util.Optional;

public class MyParkourPluginConfiguration extends DefaultConfiguration {

    private static final String
        KEY_DEFAULT_START_GAMEMODE = "course.defaultGamemode.start",
        KEY_DEFAULT_EXIT_GAMEMODE = "course.defaultGamemode.exit";

    public Optional<StartGameMode> getDefaultStartGameMode() {
        return Optional.ofNullable(StartGameMode.MAP.get(getConfiguration().getString(KEY_DEFAULT_START_GAMEMODE)));
    }

    public Optional<ExitGameMode> getDefaultExitGameMode() {
        return Optional.ofNullable(ExitGameMode.MAP.get(getConfiguration().getString(KEY_DEFAULT_EXIT_GAMEMODE)));
    }

    public void setDefaultStartGameMode(StartGameMode gm) {
        getConfiguration().set(KEY_DEFAULT_START_GAMEMODE, gm.repr);
    }

    public void setDefaultExitGameMode(ExitGameMode gm) {
        getConfiguration().set(KEY_DEFAULT_EXIT_GAMEMODE, gm.repr);
    }

    @Override
    public void generateDefaults() {
        set(KEY_DEFAULT_START_GAMEMODE, "adventure",
            "Default gamemode to set when a player begins running through a course.",
            "Valid values are: 'survival', 'adventure', 'creative', and 'none'",
            "If this is 'none', then the player's gamemode is not changed when a player runs a course.",
            "Note that a course may overwrite this behavior."
        );
        set(KEY_DEFAULT_EXIT_GAMEMODE, "previous",
            "Default gamemode to set when a player exits a course.",
            "Valid values are: 'survival', 'adventure', 'creative', 'spectator', 'previous', and 'none'",
            "If this is 'previous', then the player's previous gamemode before they began running the course will be applied.",
            "If this is 'none', then the player's gamemode is not changed when they exit a course.",
            "Note that a course may overwrite this behavior."
        );
    }

}
