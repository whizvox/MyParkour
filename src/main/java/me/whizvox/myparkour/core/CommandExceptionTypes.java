package me.whizvox.myparkour.core;

import me.whizvox.myparkour.command.TranslatableCommandExceptionType;

public class CommandExceptionTypes {

    private CommandExceptionTypes() {}

    public static final TranslatableCommandExceptionType
        SPLIT_CHECKPOINTS_UNKNOWN = new TranslatableCommandExceptionType("exception.splitCheckpoints.unknown", 1),
        SPLIT_CHECKPOINTS_CANNOT_ADD_SPLIT = new TranslatableCommandExceptionType("exception.splitCheckpoints.cannotAddSplit", 1),
        SPLIT_CHECKPOINTS_NO_DUPLICATES = new TranslatableCommandExceptionType("exception.splitCheckpoints.noDuplicates", 1),
        INVALID_COURSE_FLAG = new TranslatableCommandExceptionType("myparkour.exception.invalidCourseFlag", 1),
        MUST_BE_PLAYER = new TranslatableCommandExceptionType("exception.mustBePlayer"),
        UNKNOWN_COURSE = new TranslatableCommandExceptionType("exception.unknownCourse", 1);

}
