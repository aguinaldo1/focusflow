package io.github.aguinaldo1.focusflow.pomodoro;

import java.time.Duration;
import java.util.Objects;

public record PomodoroConfig(
        Duration focusDuration,
        Duration shortBreakDuration,
        Duration longBreakDuration,
        int focusCyclesBeforeLongBreak
) {

    public PomodoroConfig {

        Objects.requireNonNull(
                focusDuration,
                "Focus duration cannot be null."
        );

        Objects.requireNonNull(
                shortBreakDuration,
                "Short break duration cannot be null."
        );

        Objects.requireNonNull(
                longBreakDuration,
                "Long break duration cannot be null."
        );

        if (focusDuration.isZero() || focusDuration.isNegative()) {
            throw new IllegalArgumentException(
                    "Focus duration must be positive."
            );
        }

        if (shortBreakDuration.isZero() || shortBreakDuration.isNegative()) {
            throw new IllegalArgumentException(
                    "Short break duration must be positive."
            );
        }

        if (longBreakDuration.isZero() || longBreakDuration.isNegative()) {
            throw new IllegalArgumentException(
                    "Long break duration must be positive."
            );
        }

        if (focusCyclesBeforeLongBreak <= 0) {
            throw new IllegalArgumentException(
                    "Focus cycles before long break must be positive."
            );
        }
    }

    public static PomodoroConfig defaults() {
        return new PomodoroConfig(
                Duration.ofMinutes(25),
                Duration.ofMinutes(5),
                Duration.ofMinutes(15),
                4
        );
    }

    public Duration durationFor(PomodoroPhase phase) {

        Objects.requireNonNull(
                phase,
                "Pomodoro phase cannot be null."
        );

        return switch (phase) {
            case FOCUS -> focusDuration;
            case SHORT_BREAK -> shortBreakDuration;
            case LONG_BREAK -> longBreakDuration;
        };
    }
}
