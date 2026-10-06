package io.github.aguinaldo1.focusflow.pomodoro;

import java.time.Duration;
import java.util.Objects;

public class PomodoroTimer {

    private final PomodoroSession session;
    private Duration remainingTime;

    public PomodoroTimer(PomodoroSession session) {

        this.session = Objects.requireNonNull(
                session,
                "Pomodoro session cannot be null."
        );

        this.remainingTime = session.getCurrentDuration();
    }

    public synchronized void start() {
        session.start();
    }

    public synchronized void pause() {
        session.pause();
    }

    public synchronized void resume() {
        session.resume();
    }

public synchronized boolean elapse(
        Duration elapsed
) {

    if (elapsed == null
            || elapsed.isZero()
            || elapsed.isNegative()) {

        throw new IllegalArgumentException(
                "Elapsed duration must be positive."
        );
    }

    if (session.getStatus()
            != PomodoroStatus.RUNNING) {

        return false;
    }

    if (elapsed.compareTo(remainingTime) >= 0) {

        session.completeCurrentInterval();

        remainingTime =
                session.getCurrentDuration();

        return true;
    }

    remainingTime =
            remainingTime.minus(elapsed);

    return false;
}
    public synchronized void reset() {

        session.reset();

        remainingTime = session.getCurrentDuration();
    }

    public synchronized Duration getRemainingTime() {
        return remainingTime;
    }

    public synchronized PomodoroSnapshot snapshot() {

        return new PomodoroSnapshot(
                session.getPhase(),
                session.getStatus(),
                session.getCompletedFocusCycles(),
                remainingTime
        );
    }
}
