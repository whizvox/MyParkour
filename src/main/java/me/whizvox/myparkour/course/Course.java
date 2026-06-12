package me.whizvox.myparkour.course;

import me.whizvox.myparkour.MyParkour;
import me.whizvox.myparkour.util.DefaultBoolean;
import me.whizvox.myparkour.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@NotNullByDefault
public record Course(int id, String name, Component displayName, ImmutableLocation start, List<Checkpoint> checkpoints,
                     Set<CourseFlag> flags, ImmutableLocation exit, StartGameMode startGameMode,
                     ExitGameMode exitGameMode, int minY, DefaultBoolean clearInventory, boolean open) {

    public Course(int id, String name, Component displayName, ImmutableLocation start, List<Checkpoint> checkpoints,
                  Set<CourseFlag> flags, ImmutableLocation exit, StartGameMode startGameMode, ExitGameMode exitGameMode,
                  int minY, DefaultBoolean clearInventory, boolean open) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.checkpoints = Collections.unmodifiableList(checkpoints);
        this.start = start;
        this.flags = Collections.unmodifiableSet(flags);
        this.exit = exit;
        this.startGameMode = startGameMode;
        this.exitGameMode = exitGameMode;
        this.minY = minY;
        this.clearInventory = clearInventory;
        this.open = open;
    }

    public boolean shouldClearInventory() {
        return switch (clearInventory) {
            case DEFAULT -> MyParkour.inst().getPluginConfig().getDefaultClearInventory();
            case TRUE -> true;
            case FALSE -> false;
        };
    }

}
