package io.github.aguinaldo1.focusflow.pomodoro;

import java.time.Duration;

public record PomodoroSnapshot(
        PomodoroPhase phase,
        PomodoroStatus status,
        int completedFocusCycles,
        Duration remainingTime,
        Duration intervalDuration
) {
}
