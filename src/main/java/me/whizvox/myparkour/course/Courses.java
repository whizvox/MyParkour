package me.whizvox.myparkour.course;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.myparkour.course.edit.EditableCourse;
import me.whizvox.myparkour.util.Persistent;
import org.jetbrains.annotations.NotNullByDefault;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

@NotNullByDefault
public class Courses implements Persistent<List<Course>> {

    private final Int2ObjectMap<Course> byId;
    // Stores both the normal course name and the lowercased version
    private final Map<String, Course> byName;
    private int lastId;

    public Courses() {
        this.byId = new Int2ObjectOpenHashMap<>();
        this.byName = new Object2ObjectOpenHashMap<>();
        lastId = 1;
    }

    public Optional<Course> get(int id) {
        if (byId.containsKey(id)) {
            return Optional.of(byId.get(id));
        }
        return Optional.empty();
    }

    public Optional<Course> get(String name) {
        String nameLc = name.toLowerCase();
        if (byName.containsKey(nameLc)) {
            return Optional.of(byName.get(nameLc));
        }
        return Optional.empty();
    }

    public Stream<Course> stream() {
        return byId.values().stream();
    }

    public boolean isNameAvailable(String name) {
        return !byName.containsKey(name.toLowerCase());
    }

    public EditResult add(EditableCourse editableCourse) {
        if (!isNameAvailable(Objects.requireNonNull(editableCourse.getName()))) {
            return EditResult.NAME_UNAVAILABLE;
        }
        while (byId.containsKey(lastId)) {
            lastId++;
        }
        editableCourse.setId(lastId);
        var result = editableCourse.toCourse();
        if (result.left() == EditableCourse.ValidResult.VALID) {
            Course course = result.right();
            byId.put(course.id(), course);
            byName.put(course.name(), course);
            byName.put(course.name().toLowerCase(), course);
            return EditResult.SUCCESS;
        }
        return EditResult.INVALID;
    }

    public EditResult edit(EditableCourse editableCourse) {
        var result = editableCourse.toCourse();
        if (result.left() == EditableCourse.ValidResult.VALID) {
            Course course = result.right();
            Course otherCourse = byName.get(course.name().toLowerCase());
            if (otherCourse != null && otherCourse.id() != course.id()) {
                return EditResult.NAME_UNAVAILABLE;
            }
            if (remove(course.id())) {
                byId.put(course.id(), course);
                byName.put(course.name(), course);
                byName.put(course.name().toLowerCase(), course);
                return EditResult.SUCCESS;
            }
            return EditResult.NOT_FOUND;
        }
        return EditResult.INVALID;
    }

    public boolean remove(int id) {
        Course course = byId.remove(id);
        //noinspection ConstantValue
        if (course != null) {
            byName.remove(course.name());
            byName.remove(course.name().toLowerCase());
            return true;
        }
        return false;
    }

    @Override
    public List<Course> writePersistent() {
        List<Course> courses = new ArrayList<>(byId.values());
        courses.sort(Comparator.comparing(Course::id));
        return courses;
    }

    @Override
    public void readPersistent(List<Course> courses) {
        byId.clear();
        byName.clear();
        courses.forEach(course -> {
            byId.put(course.id(), course);
            byName.put(course.name(), course);
            byName.put(course.name().toLowerCase(), course);
        });
    }

    @Override
    public Type getPersistentType() {
        return new TypeToken<ArrayList<Course>>(){}.getType();
    }

    public enum EditResult {
        SUCCESS,
        INVALID,
        NAME_UNAVAILABLE,
        NOT_FOUND
    }

}
