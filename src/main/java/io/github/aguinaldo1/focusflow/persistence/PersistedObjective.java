package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.ObjectiveStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroPhase;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;

import java.time.Instant;
import java.util.UUID;

public record PersistedObjective(
        UUID id,
        String name,
        String description,
        ObjectiveStatus status,
        Instant createdAt,
        Instant closedAt,
        PomodoroPhase pomodoroPhase,
        PomodoroStatus pomodoroStatus,
        int completedFocusCycles,
        long remainingSeconds
) {
}
