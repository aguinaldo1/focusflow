package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroConfig;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSession;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroTimer;

import java.time.Duration;
import java.util.Objects;

public final class ObjectiveRestorer {

    public Objective restore(
            PersistedObjective persisted
    ) {

        Objects.requireNonNull(
                persisted,
                "persisted"
        );

        PomodoroSession session =
                PomodoroSession.restore(
                        PomodoroConfig.defaults(),
                        persisted.pomodoroPhase(),
                        persisted.pomodoroStatus(),
                        persisted.completedFocusCycles()
                );

        PomodoroTimer timer =
                PomodoroTimer.restore(
                        session,
                        Duration.ofSeconds(
                                persisted.remainingSeconds()
                        )
                );

        return Objective.restore(
                persisted.id(),
                persisted.name(),
                persisted.description(),
                persisted.status(),
                persisted.createdAt(),
                persisted.closedAt(),
                timer
        );
    }
}
