package me.whizvox.myparkour.course.edit;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.whizvox.myparkour.course.Course;
import me.whizvox.myparkour.course.Courses;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CourseEdits {

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

    public Optional<EditableCourse> createEditableCourse(Player player, int id) {
        if (!isPlayerEditing(player) && !isCourseBeingEdited(id)) {
            Course course = courses.get(id);
            //noinspection ConstantValue
            if (course != null) {
                EditableCourse edit = new EditableCourse(course);
                courseEdits.put(player.getUniqueId(), edit);
                courseEditIds.add(id);
                return Optional.of(edit);
            }
        }
        return Optional.empty();
    }

    public boolean stopEditingCourse(Player player, boolean saveChanges) {
        EditableCourse editableCourse = courseEdits.remove(player.getUniqueId());
        if (editableCourse != null) {
            courseEditIds.remove(editableCourse.getId());
            if (saveChanges) {
                if (editableCourse.isNew()) {
                    return courses.add(editableCourse);
                }
                return courses.edit(editableCourse);
            }
            return true;
        }
        return false;
    }

}
