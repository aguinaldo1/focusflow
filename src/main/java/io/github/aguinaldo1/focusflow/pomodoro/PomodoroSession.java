package io.github.aguinaldo1.focusflow.pomodoro;

import java.time.Duration;
import java.util.Objects;

public final class PomodoroSession {

    private final PomodoroConfig config;

    private PomodoroPhase phase;
    private PomodoroStatus status;
    private int completedFocusCycles;

    public PomodoroSession() {

        this(
                PomodoroConfig.defaults()
        );
    }

    public PomodoroSession(
            PomodoroConfig config
    ) {

        this(
                config,
                PomodoroPhase.FOCUS,
                PomodoroStatus.IDLE,
                0
        );
    }

    private PomodoroSession(
            PomodoroConfig config,
            PomodoroPhase phase,
            PomodoroStatus status,
            int completedFocusCycles
    ) {

        this.config =
                Objects.requireNonNull(
                        config,
                        "config"
                );

        this.phase =
                Objects.requireNonNull(
                        phase,
                        "phase"
                );

        this.status =
                Objects.requireNonNull(
                        status,
                        "status"
                );

        if (completedFocusCycles < 0) {

            throw new IllegalArgumentException(
                    "Completed focus cycles cannot be negative."
            );
        }

        this.completedFocusCycles =
                completedFocusCycles;
    }

    public static PomodoroSession restore(
            PomodoroConfig config,
            PomodoroPhase phase,
            PomodoroStatus status,
            int completedFocusCycles
    ) {

        return new PomodoroSession(
                config,
                phase,
                status,
                completedFocusCycles
        );
    }

    public void start() {

        if (status != PomodoroStatus.IDLE) {

            throw new IllegalStateException(
                    "Pomodoro can only start from IDLE."
            );
        }

        status =
                PomodoroStatus.RUNNING;
    }

    public void pause() {

        if (status != PomodoroStatus.RUNNING) {

            throw new IllegalStateException(
                    "Pomodoro can only pause while RUNNING."
            );
        }

        status =
                PomodoroStatus.PAUSED;
    }

    public void resume() {

        if (status != PomodoroStatus.PAUSED) {

            throw new IllegalStateException(
                    "Pomodoro can only resume from PAUSED."
            );
        }

        status =
                PomodoroStatus.RUNNING;
    }

    public void completeCurrentInterval() {

        if (status != PomodoroStatus.RUNNING) {

            throw new IllegalStateException(
                    "Only a running interval can be completed."
            );
        }

        if (phase == PomodoroPhase.FOCUS) {

            completedFocusCycles++;

            if (
                    completedFocusCycles
                            % config.focusCyclesBeforeLongBreak()
                            == 0
            ) {

                phase =
                        PomodoroPhase.LONG_BREAK;

            } else {

                phase =
                        PomodoroPhase.SHORT_BREAK;
            }

        } else {

            phase =
                    PomodoroPhase.FOCUS;
        }

        status =
                PomodoroStatus.IDLE;
    }

    public void reset() {

        phase =
                PomodoroPhase.FOCUS;

        status =
                PomodoroStatus.IDLE;

        completedFocusCycles =
                0;
    }

    public Duration getCurrentDuration() {

        return config.durationFor(
                phase
        );
    }

    public PomodoroConfig getConfig() {
        return config;
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
}
