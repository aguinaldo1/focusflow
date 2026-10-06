package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroPhase;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ObjectiveRestorerTest {

    @Test
    void shouldRestoreActiveObjectiveWithPomodoroProgress() {

        UUID id =
                UUID.randomUUID();

        Instant createdAt =
                Instant.parse(
                        "2026-10-05T20:00:00Z"
                );

        PersistedObjective persisted =
                new PersistedObjective(
                        id,
                        "Curso Java Guanabara",
                        "Aprender fundamentos de Java.",
                        ObjectiveStatus.ACTIVE,
                        createdAt,
                        null,
                        PomodoroPhase.FOCUS,
                        PomodoroStatus.PAUSED,
                        2,
                        1122
                );

        ObjectiveRestorer restorer =
                new ObjectiveRestorer();

        Objective restored =
                restorer.restore(
                        persisted
                );

        assertEquals(
                id,
                restored.getId()
        );

        assertEquals(
                "Curso Java Guanabara",
                restored.getName()
        );

        assertEquals(
                ObjectiveStatus.ACTIVE,
                restored.getStatus()
        );

        assertEquals(
                createdAt,
                restored.getCreatedAt()
        );

        assertNull(
                restored.getClosedAt()
        );

        assertEquals(
                PomodoroPhase.FOCUS,
                restored.getTimer()
                        .snapshot()
                        .phase()
        );

        assertEquals(
                PomodoroStatus.PAUSED,
                restored.getTimer()
                        .snapshot()
                        .status()
        );

        assertEquals(
                2,
                restored.getTimer()
                        .snapshot()
                        .completedFocusCycles()
        );

        assertEquals(
                1122,
                restored.getTimer()
                        .snapshot()
                        .remainingTime()
                        .toSeconds()
        );
    }

    @Test
    void shouldRestoreCompletedObjective() {

        Instant createdAt =
                Instant.parse(
                        "2026-10-05T20:00:00Z"
                );

        Instant closedAt =
                Instant.parse(
                        "2026-10-05T22:00:00Z"
                );

        PersistedObjective persisted =
                new PersistedObjective(
                        UUID.randomUUID(),
                        "Leitura Clean Code",
                        "",
                        ObjectiveStatus.COMPLETED,
                        createdAt,
                        closedAt,
                        PomodoroPhase.FOCUS,
                        PomodoroStatus.IDLE,
                        4,
                        1500
                );

        Objective restored =
                new ObjectiveRestorer()
                        .restore(
                                persisted
                        );

        assertEquals(
                ObjectiveStatus.COMPLETED,
                restored.getStatus()
        );

        assertEquals(
                closedAt,
                restored.getClosedAt()
        );

        assertEquals(
                4,
                restored.getTimer()
                        .snapshot()
                        .completedFocusCycles()
        );
    }
@Test
void shouldRestoreRunningActiveObjectiveAsPaused() {

    PersistedObjective persisted =
            new PersistedObjective(
                    UUID.randomUUID(),
                    "Curso Java Guanabara",
                    "Continuar estudos de Java.",
                    ObjectiveStatus.ACTIVE,
                    Instant.parse(
                            "2026-10-05T20:00:00Z"
                    ),
                    null,
                    PomodoroPhase.FOCUS,
                    PomodoroStatus.RUNNING,
                    3,
                    1122
            );

    Objective restored =
            new ObjectiveRestorer()
                    .restore(
                            persisted
                    );

    assertEquals(
            PomodoroStatus.PAUSED,
            restored
                    .getTimer()
                    .snapshot()
                    .status()
    );

    assertEquals(
            1122,
            restored
                    .getTimer()
                    .snapshot()
                    .remainingTime()
                    .toSeconds()
    );

    assertEquals(
            3,
            restored
                    .getTimer()
                    .snapshot()
                    .completedFocusCycles()
    );
}
}
