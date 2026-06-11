package me.whizvox.myparkour.course.edit;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.Courses;
import me.whizvox.myparkour.util.Persistent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

public class CourseEdits implements Persistent<List<CourseEdits.Entry>> {

    private final Courses courses;
    private final Map<UUID, EditableCourse> courseEdits;
    private final IntSet courseEditIds;

    public CourseEdits(Courses courses) {
        this.courses = courses;
        courseEdits = new Object2ObjectArrayMap<>();
        courseEditIds = new IntArraySet();
    }

    public boolean isPlayerEditing(Player player) {
        return courseEdits.containsKey(player.getUniqueId());
    }

    public boolean isCourseBeingEdited(int id) {
        return courseEditIds.contains(id);
    }

    public Optional<EditableCourse> getEditing(Player player) {
        return Optional.ofNullable(courseEdits.get(player.getUniqueId()));
    }

    public Optional<UUID> getPlayer(int courseId) {
        return courseEdits.entrySet().stream()
            .filter(entry -> entry.getValue().getId() == courseId)
            .map(Map.Entry::getKey)
            .findAny();
    }

    public BeginEditResult setEditingCourse(Player player, EditableCourse editableCourse) {
        if (isPlayerEditing(player)) {
            return BeginEditResult.PLAYER_EDITING;
        }
        if (!editableCourse.isNew() && isCourseBeingEdited(editableCourse.getId())) {
            return BeginEditResult.COURSE_EDITED;
        }
        courseEdits.put(player.getUniqueId(), editableCourse);
        if (!editableCourse.isNew()) {
            courseEditIds.add(editableCourse.getId());
        }
        return BeginEditResult.SUCCESS;
    }

    public Pair<BeginEditResult, Optional<EditableCourse>> createEditableCourse(Player player, int id) {
        if (isPlayerEditing(player)) {
            return Pair.of(BeginEditResult.PLAYER_EDITING, Optional.empty());
        }
        if (isCourseBeingEdited(id)) {
            return Pair.of(BeginEditResult.COURSE_EDITED, Optional.empty());
        }
        var courseOp = courses.get(id);
        if (courseOp.isEmpty()) {
            return Pair.of(BeginEditResult.COURSE_NOT_EXIST, Optional.empty());
        }
        Course course = courseOp.get();
        EditableCourse edit = new EditableCourse(course);
        courseEdits.put(player.getUniqueId(), edit);
        courseEditIds.add(id);
        return Pair.of(BeginEditResult.SUCCESS, Optional.of(edit));
    }

    /**
     * Remove a course that's currently being edited from the list of edited courses.
     * @param player The player who's editing the course
     * @param saveChanges Whether to save the course. Setting this to <code>false</code> will discard any edits.
     * @return A {@link me.whizvox.myparkour.course.Courses.EditResult}, or <code>null</code> if the player was not
     * editing a course.
     */
    public @Nullable Courses.EditResult stopEditingCourse(Player player, boolean saveChanges) {
        EditableCourse editableCourse = courseEdits.get(player.getUniqueId());
        if (editableCourse != null) {
            if (saveChanges) {
                Courses.EditResult result;
                if (editableCourse.isNew()) {
                    result = courses.add(editableCourse);
                } else {
                    result = courses.edit(editableCourse);
                }
                if (result != Courses.EditResult.SUCCESS) {
                    return result;
                }
            }
            courseEditIds.remove(editableCourse.getId());
            courseEdits.remove(player.getUniqueId());
            return Courses.EditResult.SUCCESS;
        }
        return null;
    }

    @Override
    public List<Entry> writePersistent() {
        return courseEdits.entrySet().stream().map(entry -> new Entry(entry.getKey(), entry.getValue())).toList();
    }

    @Override
    public void readPersistent(List<Entry> entries) {
        courseEdits.clear();
        courseEditIds.clear();
        entries.forEach(entry -> {
            courseEdits.put(entry.player, entry.course);
            if (!entry.course.isNew()) {
                courseEditIds.add(entry.course.getId());
            }
        });
    }

    @Override
    public Type getPersistentType() {
        return new TypeToken<ArrayList<Entry>>() {
        }.getType();
    }

    public enum BeginEditResult {
        SUCCESS,
        PLAYER_EDITING,
        COURSE_EDITED,
        COURSE_NOT_EXIST
    }

    public record Entry(UUID player, EditableCourse course) {
    }

}
