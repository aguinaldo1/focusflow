package io.github.aguinaldo1.focusflow.pomodoro;

import java.time.Duration;
import java.util.Objects;

public class PomodoroSession {

    private final PomodoroConfig config;

    private PomodoroPhase phase;
    private PomodoroStatus status;
    private int completedFocusCycles;

    public PomodoroSession() {
        this(PomodoroConfig.defaults());
    }

    public PomodoroSession(PomodoroConfig config) {

        this.config = Objects.requireNonNull(
                config,
                "Pomodoro configuration cannot be null."
        );

        this.phase = PomodoroPhase.FOCUS;
        this.status = PomodoroStatus.IDLE;
        this.completedFocusCycles = 0;
    }

    public void start() {

        if (status != PomodoroStatus.IDLE) {
            throw new IllegalStateException(
                    "Pomodoro can only start from IDLE state."
            );
        }

        status = PomodoroStatus.RUNNING;
    }

    public void pause() {

        if (status != PomodoroStatus.RUNNING) {
            throw new IllegalStateException(
                    "Only a running Pomodoro can be paused."
            );
        }

        status = PomodoroStatus.PAUSED;
    }

    public void resume() {

        if (status != PomodoroStatus.PAUSED) {
            throw new IllegalStateException(
                    "Only a paused Pomodoro can be resumed."
            );
        }

        status = PomodoroStatus.RUNNING;
    }

    public void completeCurrentInterval() {

        if (status != PomodoroStatus.RUNNING) {
            throw new IllegalStateException(
                    "Only a running interval can be completed."
            );
        }

        if (phase == PomodoroPhase.FOCUS) {

            completedFocusCycles++;

            if (completedFocusCycles
                    % config.focusCyclesBeforeLongBreak() == 0) {

                phase = PomodoroPhase.LONG_BREAK;

            } else {

                phase = PomodoroPhase.SHORT_BREAK;
            }

        } else {

            phase = PomodoroPhase.FOCUS;
        }

        status = PomodoroStatus.IDLE;
    }

    public void reset() {

        phase = PomodoroPhase.FOCUS;
        status = PomodoroStatus.IDLE;
        completedFocusCycles = 0;
    }

    public Duration getCurrentDuration() {
        return config.durationFor(phase);
    }

    public PomodoroPhase getPhase() {
        return phase;
    }

    public PomodoroStatus getStatus() {
        return status;
    }

    public int getCompletedFocusCycles() {
        return completedFocusCycles;
    }

    public PomodoroConfig getConfig() {
        return config;
    }
}
