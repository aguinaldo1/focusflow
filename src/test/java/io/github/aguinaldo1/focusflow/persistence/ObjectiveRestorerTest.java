package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveComplexity;
import io.github.aguinaldo1.focusflow.objective.ObjectiveStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroPhase;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
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
                restored
                        .getTimer()
                        .snapshot()
                        .phase()
        );

        assertEquals(
                PomodoroStatus.PAUSED,
                restored
                        .getTimer()
                        .snapshot()
                        .status()
        );

        assertEquals(
                2,
                restored
                        .getTimer()
                        .snapshot()
                        .completedFocusCycles()
        );

        assertEquals(
                1122,
                restored
                        .getTimer()
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
                restored
                        .getTimer()
                        .snapshot()
                        .completedFocusCycles()
        );
    }

    @Test
    void shouldRestoreLegacyRunningActiveObjectiveAsPaused() {

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

    @Test
    void shouldRestoreComplexityAndAdjustedPlannedCycles() {

        PersistedObjective persisted =
                new PersistedObjective(
                        UUID.randomUUID(),
                        "Kubernetes avançado",
                        "",
                        ObjectiveStatus.ACTIVE,
                        Instant.parse(
                                "2026-10-06T10:00:00Z"
                        ),
                        null,
                        ObjectiveComplexity.HARD,
                        7,
                        PomodoroPhase.FOCUS,
                        PomodoroStatus.PAUSED,
                        2,
                        900
                );

        Objective restored =
                new ObjectiveRestorer()
                        .restore(
                                persisted
                        );

        assertEquals(
                ObjectiveComplexity.HARD,
                restored.getComplexity()
        );

        assertEquals(
                7,
                restored.getPlannedFocusCycles()
        );

        assertEquals(
                2,
                restored
                        .getTimer()
                        .snapshot()
                        .completedFocusCycles()
        );
    }

    @Test
    void shouldContinueRunningBreakAfterOfflineTime() {

        Instant savedAt =
                Instant.parse(
                        "2026-10-06T18:00:00Z"
                );

        Clock clock =
                Clock.fixed(
                        Instant.parse(
                                "2026-10-06T18:01:00Z"
                        ),
                        ZoneOffset.UTC
                );

        PersistedObjective persisted =
                new PersistedObjective(
                        UUID.randomUUID(),
                        "Curso Java",
                        "",
                        ObjectiveStatus.ACTIVE,
                        Instant.parse(
                                "2026-10-06T17:00:00Z"
                        ),
                        null,
                        ObjectiveComplexity.MEDIUM,
                        4,
                        PomodoroPhase.SHORT_BREAK,
                        PomodoroStatus.RUNNING,
                        1,
                        240,
                        savedAt
                );

        Objective restored =
                new ObjectiveRestorer(
                        clock
                )
                        .restore(
                                persisted
                        );

        assertEquals(
                PomodoroPhase.SHORT_BREAK,
                restored
                        .getTimer()
                        .snapshot()
                        .phase()
        );

        assertEquals(
                PomodoroStatus.RUNNING,
                restored
                        .getTimer()
                        .snapshot()
                        .status()
        );

        assertEquals(
                180,
                restored
                        .getTimer()
                        .snapshot()
                        .remainingTime()
                        .toSeconds()
        );

        assertEquals(
                1,
                restored
                        .getTimer()
                        .snapshot()
                        .completedFocusCycles()
        );
    }

    @Test
    void shouldReleaseNextFocusWhenBreakFinishedWhileAppWasClosed() {

        Instant savedAt =
                Instant.parse(
                        "2026-10-06T18:00:00Z"
                );

        Clock clock =
                Clock.fixed(
                        Instant.parse(
                                "2026-10-06T18:05:00Z"
                        ),
                        ZoneOffset.UTC
                );

        PersistedObjective persisted =
                new PersistedObjective(
                        UUID.randomUUID(),
                        "Curso Java",
                        "",
                        ObjectiveStatus.ACTIVE,
                        Instant.parse(
                                "2026-10-06T17:00:00Z"
                        ),
                        null,
                        ObjectiveComplexity.MEDIUM,
                        4,
                        PomodoroPhase.SHORT_BREAK,
                        PomodoroStatus.RUNNING,
                        1,
                        240,
                        savedAt
                );

        Objective restored =
                new ObjectiveRestorer(
                        clock
                )
                        .restore(
                                persisted
                        );

        assertEquals(
                PomodoroPhase.FOCUS,
                restored
                        .getTimer()
                        .snapshot()
                        .phase()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                restored
                        .getTimer()
                        .snapshot()
                        .status()
        );

        assertEquals(
                1500,
                restored
                        .getTimer()
                        .snapshot()
                        .remainingTime()
                        .toSeconds()
        );

        assertEquals(
                1,
                restored
                        .getTimer()
                        .snapshot()
                        .completedFocusCycles()
        );
    }

    @Test
    void shouldPauseRunningFocusWithoutConsumingOfflineTime() {

        Instant savedAt =
                Instant.parse(
                        "2026-10-06T18:00:00Z"
                );

        Clock clock =
                Clock.fixed(
                        Instant.parse(
                                "2026-10-06T18:30:00Z"
                        ),
                        ZoneOffset.UTC
                );

        PersistedObjective persisted =
                new PersistedObjective(
                        UUID.randomUUID(),
                        "Curso Java",
                        "",
                        ObjectiveStatus.ACTIVE,
                        Instant.parse(
                                "2026-10-06T17:00:00Z"
                        ),
                        null,
                        ObjectiveComplexity.MEDIUM,
                        4,
                        PomodoroPhase.FOCUS,
                        PomodoroStatus.RUNNING,
                        2,
                        1122,
                        savedAt
                );

        Objective restored =
                new ObjectiveRestorer(
                        clock
                )
                        .restore(
                                persisted
                        );

        assertEquals(
                PomodoroPhase.FOCUS,
                restored
                        .getTimer()
                        .snapshot()
                        .phase()
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
                2,
                restored
                        .getTimer()
                        .snapshot()
                        .completedFocusCycles()
        );
    }
}
