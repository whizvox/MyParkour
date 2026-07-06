package me.whizvox.myparkour.course.edit;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record CourseEditResult(Type type, @Nullable Object details) {

    public CourseEditResult(Type type) {
        this(type, Optional.empty());
    }

    public Optional<EditableCourse.ValidResult> getValidResult() {
        if (details != null && EditableCourse.ValidResult.class.isAssignableFrom(details.getClass())) {
            return Optional.of((EditableCourse.ValidResult) details);
        }
        return Optional.empty();
    }

    public static CourseEditResult success() {
        return new CourseEditResult(Type.SUCCESS);
    }

    public static CourseEditResult invalid(EditableCourse.ValidResult result) {
        return new CourseEditResult(Type.INVALID, result);
    }

    public static CourseEditResult nameUnavailable() {
        return new CourseEditResult(Type.NAME_UNAVAILABLE);
    }

    public static CourseEditResult notFound() {
        return new CourseEditResult(Type.NOT_FOUND);
    }

    public static CourseEditResult notEditing() {
        return new CourseEditResult(Type.NOT_EDITING);
    }

    public enum Type {
        SUCCESS,
        INVALID,
        NAME_UNAVAILABLE,
        NOT_FOUND,
        NOT_EDITING
    }

}
