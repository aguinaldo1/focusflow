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

    public void start() {
        session.start();
    }

    public void pause() {
        session.pause();
    }

    public void resume() {
        session.resume();
    }

    public void elapse(Duration elapsedTime) {

        Objects.requireNonNull(
                elapsedTime,
                "Elapsed time cannot be null."
        );

        if (elapsedTime.isNegative() || elapsedTime.isZero()) {
            throw new IllegalArgumentException(
                    "Elapsed time must be positive."
            );
        }

        if (session.getStatus() != PomodoroStatus.RUNNING) {
            return;
        }

        if (elapsedTime.compareTo(remainingTime) >= 0) {

            session.completeCurrentInterval();

            remainingTime = session.getCurrentDuration();

            return;
        }

        remainingTime = remainingTime.minus(elapsedTime);
    }

    public void reset() {

        session.reset();

        remainingTime = session.getCurrentDuration();
    }

    public Duration getRemainingTime() {
        return remainingTime;
    }

    public PomodoroSession getSession() {
        return session;
    }
}
