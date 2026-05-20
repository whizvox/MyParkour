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

@NotNullByDefault
public class Courses implements Persistent<List<Course>> {

    private final Int2ObjectMap<Course> byId;
    // Stores both the normal course name and the lowercased version
    private final Map<String, Course> byName;
    private int lastId;


    public Courses() {
        this.byId = new Int2ObjectOpenHashMap<>();
        this.byName = new Object2ObjectOpenHashMap<>();
        lastId = 0;
    }

    public Course get(int id) {
        return byId.get(id);
    }

    public Course get(String name) {
        return byName.get(name.toLowerCase());
    }

    public boolean isNameAvailable(String name) {
        return !byName.containsKey(name.toLowerCase());
    }

    public boolean add(EditableCourse editableCourse) {
        if (!editableCourse.isValid() || !isNameAvailable(Objects.requireNonNull(editableCourse.getName()))) {
            return false;
        }
        while (byId.containsKey(lastId)) {
            lastId++;
        }
        editableCourse.setId(lastId);
        return editableCourse.toCourse().map(course -> {
            byId.put(course.id(), course);
            byName.put(course.name(), course);
            byName.put(course.name().toLowerCase(), course);
            return true;
        }).orElse(false);
    }

    public boolean edit(EditableCourse editableCourse) {
        return editableCourse.toCourse().map(course -> {
            if (remove(course.id())) {
                byId.put(course.id(), course);
                byName.put(course.name(), course);
                byName.put(course.name().toLowerCase(), course);
                return true;
            }
            return false;
        }).orElse(false);
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
