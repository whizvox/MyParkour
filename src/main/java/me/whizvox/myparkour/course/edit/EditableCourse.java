package me.whizvox.myparkour.course.edit;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.whizvox.myparkour.course.*;
import me.whizvox.myparkour.util.DefaultBoolean;
import me.whizvox.myparkour.util.ImmutableLocation;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EditableCourse {

    private int id;
    private @Nullable String name;
    private @Nullable Component displayName;
    private final List<Checkpoint> checkpoints;
    private @Nullable ImmutableLocation start;
    private final Set<CourseFlag> flags;
    private @Nullable ImmutableLocation exit;
    private StartGameMode startGameMode;
    private ExitGameMode exitGameMode;
    private int minY;
    private DefaultBoolean clearInventory;
    private boolean open;

    public EditableCourse() {
        id = 0;
        name = "";
        displayName = Component.empty();
        checkpoints = new ArrayList<>();
        start = null;
        flags = new ObjectArraySet<>();
        exit = null;
        startGameMode = StartGameMode.DEFAULT;
        exitGameMode = ExitGameMode.DEFAULT;
        minY = -64;
        clearInventory = DefaultBoolean.DEFAULT;
        open = false;
    }

    public EditableCourse(Course orig) {
        id = orig.id();
        name = orig.name();
        displayName = orig.displayName();
        checkpoints = new ArrayList<>();
        checkpoints.addAll(orig.checkpoints());
        start = orig.start();
        flags = new ObjectArraySet<>();
        flags.addAll(orig.flags());
        exit = orig.exit();
        startGameMode = orig.startGameMode();
        exitGameMode = orig.exitGameMode();
        minY = orig.minY();
        clearInventory = orig.clearInventory();
        open = orig.open();
    }

    public boolean isNew() {
        return id == 0;
    }

    public int getId() {
        return id;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable Component getDisplayName() {
        return displayName;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public @Nullable ImmutableLocation getStart() {
        return start;
    }

    public Set<CourseFlag> getFlags() {
        return Collections.unmodifiableSet(flags);
    }

    public @Nullable ImmutableLocation getExit() {
        return exit;
    }

    public StartGameMode getStartGameMode() {
        return startGameMode;
    }

    public ExitGameMode getExitGameMode() {
        return exitGameMode;
    }

    public int getMinY() {
        return minY;
    }

    public DefaultBoolean shouldClearInventory() {
        return clearInventory;
    }

    public boolean isOpen() {
        return open;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    public int addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
        return checkpoints.size() - 1;
    }

    public boolean insertCheckpoint(int index, Checkpoint checkpoint) {
        if (index >= 0 && index <= checkpoints.size()) {
            checkpoints.add(index, checkpoint);
            return true;
        }
        return false;
    }

    public boolean removeCheckpoint(int index) {
        if (index >= 0 && index < checkpoints.size()) {
            checkpoints.remove(index);
            return true;
        }
        return false;
    }

    public void setStart(ImmutableLocation start) {
        this.start = start;
    }

    public boolean addFlag(CourseFlag flag) {
        return flags.add(flag);
    }

    public boolean removeFlag(CourseFlag flag) {
        return flags.remove(flag);
    }

    public void setExit(ImmutableLocation exit) {
        this.exit = exit;
    }

    public void setStartGameMode(StartGameMode startGameMode) {
        this.startGameMode = startGameMode;
    }

    public void setExitGameMode(ExitGameMode exitGameMode) {
        this.exitGameMode = exitGameMode;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public void setClearInventory(DefaultBoolean clearInventory) {
        this.clearInventory = clearInventory;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public ValidResult checkValid() {
        if (isNew()) {
            return ValidResult.MISSING_ID;
        }
        if (name == null || name.isBlank()) {
            return ValidResult.MISSING_NAME;
        }
        if (displayName == null) {
            return ValidResult.MISSING_DISPLAY_NAME;
        }
        if (checkpoints.isEmpty()) {
            return ValidResult.NO_CHECKPOINTS;
        }
        if (start == null) {
            return ValidResult.MISSING_START;
        }
        if (exit == null) {
            return ValidResult.MISSING_EXIT;
        }
        return ValidResult.VALID;
    }

    public Pair<ValidResult, Course> toCourse() {
        ValidResult result = checkValid();
        if (result == ValidResult.VALID) {
            //noinspection DataFlowIssue
            return Pair.of(ValidResult.VALID, new Course(id, name, displayName, start, checkpoints, flags, exit,
                startGameMode, exitGameMode, minY, clearInventory, open));
        }
        return Pair.of(result, null);
    }

    public enum ValidResult {
        VALID,
        MISSING_ID,
        MISSING_NAME,
        MISSING_DISPLAY_NAME,
        NO_CHECKPOINTS,
        MISSING_START,
        MISSING_EXIT
    }

}
