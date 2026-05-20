package me.whizvox.myparkour.course.edit;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.whizvox.myparkour.course.Checkpoint;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.CourseFlag;
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
    private boolean open;

    public EditableCourse() {
        id = 0;
        name = "";
        displayName = "";
        checkpoints = new ArrayList<>();
        start = null;
        flags = new ObjectArraySet<>();
        exit = null;
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
        return Collections.unmodifiableList(checkpoints);
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

    public void setExit(ImmutableLocation exit) {
        this.exit = exit;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isValid() {
        return id > 0 && name != null && !name.isBlank() && displayName != null && !displayName.isBlank() &&
            !checkpoints.isEmpty() && start != null && exit != null;
    }

    public Optional<Course> toCourse() {
        if (isValid()) {
            //noinspection DataFlowIssue
            return Optional.of(new Course(id, name, displayName, start, checkpoints, flags, exit, open));
        }
        return Optional.empty();
    }

}
