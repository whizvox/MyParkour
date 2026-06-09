package me.whizvox.myparkour.course.edit;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.whizvox.myparkour.course.*;
import me.whizvox.myparkour.util.ImmutableLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EditableCourse {

    private int id;
    private @Nullable String name;
    private @Nullable String displayName;
    private final List<Checkpoint> checkpoints;
    private @Nullable ImmutableLocation start;
    private final Set<CourseFlag> flags;
    private @Nullable ImmutableLocation exit;
    private StartGameMode startGameMode;
    private ExitGameMode exitGameMode;
    private boolean open;

    public EditableCourse() {
        id = 0;
        name = "";
        displayName = "";
        checkpoints = new ArrayList<>();
        start = null;
        flags = new ObjectArraySet<>();
        exit = null;
        startGameMode = StartGameMode.DEFAULT;
        exitGameMode = ExitGameMode.DEFAULT;
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

    public @Nullable String getDisplayName() {
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

    public boolean isOpen() {
        return open;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
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
        if (displayName == null || displayName.isBlank()) {
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
                startGameMode, exitGameMode, open));
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
