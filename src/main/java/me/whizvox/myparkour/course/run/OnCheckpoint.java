package me.whizvox.myparkour.course.run;

public interface OnCheckpoint {

    void onCheckpoint(Cause cause, int checkpointIndex);

    enum Cause {
        NEXT,
        FAIL,
        RESTART
    }

}
