package me.whizvox.myparkour.course;

import com.google.common.reflect.TypeToken;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.whizvox.myparkour.course.edit.CourseEditResult;
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

    public CourseEditResult add(EditableCourse editableCourse) {
        if (!isNameAvailable(editableCourse.getName())) {
            return CourseEditResult.nameUnavailable();
        }
        EditableCourse.ValidResult result = editableCourse.checkValid();
        if (result == EditableCourse.ValidResult.VALID) {
            while (byId.containsKey(lastId)) {
                lastId++;
            }
            editableCourse.setId(lastId);
            Course course = editableCourse.toCourse();
            byId.put(course.id(), course);
            byName.put(course.name(), course);
            byName.put(course.name().toLowerCase(), course);
            return CourseEditResult.success();
        }
        return CourseEditResult.invalid(result);
    }

    public CourseEditResult edit(EditableCourse editableCourse) {
        Course otherCourse = byName.get(editableCourse.getName().toLowerCase());
        if (otherCourse != null && otherCourse.id() != editableCourse.getId()) {
            return CourseEditResult.nameUnavailable();
        }
        EditableCourse.ValidResult result = editableCourse.checkValid();
        if (result == EditableCourse.ValidResult.VALID) {
            Course course = editableCourse.toCourse();
            if (remove(course.id())) {
                byId.put(course.id(), course);
                byName.put(course.name(), course);
                byName.put(course.name().toLowerCase(), course);
                return CourseEditResult.success();
            }
            return CourseEditResult.notFound();
        }
        return CourseEditResult.invalid(result);
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

}
