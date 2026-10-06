package io.github.aguinaldo1.focusflow.pomodoro;

import java.time.Duration;
import java.util.Objects;

public final class PomodoroTimer {

    private final PomodoroSession session;

    private Duration remainingTime;

    public PomodoroTimer(
            PomodoroSession session
    ) {

        this(
                session,
                session.getCurrentDuration()
        );
    }

    private PomodoroTimer(
            PomodoroSession session,
            Duration remainingTime
    ) {

        this.session =
                Objects.requireNonNull(
                        session,
                        "session"
                );

        this.remainingTime =
                validateRemainingTime(
                        remainingTime,
                        session
                );
    }

    public static PomodoroTimer restore(
            PomodoroSession session,
            Duration remainingTime
    ) {

        return new PomodoroTimer(
                session,
                remainingTime
        );
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

        if (
                elapsed == null
                        || elapsed.isZero()
                        || elapsed.isNegative()
        ) {

            throw new IllegalArgumentException(
                    "Elapsed duration must be positive."
            );
        }

        if (
                session.getStatus()
                        != PomodoroStatus.RUNNING
        ) {

            return false;
        }

        if (
                elapsed.compareTo(
                        remainingTime
                ) >= 0
        ) {

            session.completeCurrentInterval();

            remainingTime =
                    session.getCurrentDuration();

            return true;
        }

        remainingTime =
                remainingTime.minus(
                        elapsed
                );

        return false;
    }

    public synchronized void reset() {

        session.reset();

        remainingTime =
                session.getCurrentDuration();
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

    private static Duration validateRemainingTime(
            Duration remainingTime,
            PomodoroSession session
    ) {

        Objects.requireNonNull(
                remainingTime,
                "remainingTime"
        );

        if (
                remainingTime.isZero()
                        || remainingTime.isNegative()
        ) {

            throw new IllegalArgumentException(
                    "Remaining time must be positive."
            );
        }

        if (
                remainingTime.compareTo(
                        session.getCurrentDuration()
                ) > 0
        ) {

            throw new IllegalArgumentException(
                    "Remaining time cannot exceed interval duration."
            );
        }

        return remainingTime;
    }
}
