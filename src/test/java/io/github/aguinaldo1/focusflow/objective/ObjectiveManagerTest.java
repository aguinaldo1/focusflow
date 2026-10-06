package io.github.aguinaldo1.focusflow.objective;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSession;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroTimer;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectiveManagerTest {

    @Test
    void shouldAutomaticallySelectFirstObjective() {

        ObjectiveManager manager =
                new ObjectiveManager();

        Objective objective =
                manager.addObjective(
                        "Curso Java Guanabara",
                        "Estudar fundamentos de Java."
                );

        assertSame(
                objective,
                manager.getSelectedObjective()
                        .orElseThrow()
        );
    }

    @Test
    void shouldAllowUpToFiveActiveObjectives() {

        ObjectiveManager manager =
                new ObjectiveManager();

        for (
                int index = 1;
                index <= 5;
                index++
        ) {

            manager.addObjective(
                    "Objetivo " + index,
                    ""
            );
        }

        assertEquals(
                5,
                manager.getActiveObjectiveCount()
        );
    }

    @Test
    void shouldRejectSixthActiveObjective() {

        ObjectiveManager manager =
                new ObjectiveManager();

        for (
                int index = 1;
                index <= 5;
                index++
        ) {

            manager.addObjective(
                    "Objetivo " + index,
                    ""
            );
        }

        assertThrows(
                IllegalStateException.class,
                () ->
                        manager.addObjective(
                                "Sexto objetivo",
                                ""
                        )
        );
    }

    @Test
    void shouldPauseCurrentObjectiveWhenSelectingAnother() {

        ObjectiveManager manager =
                new ObjectiveManager();

        Objective javaObjective =
                manager.addObjective(
                        "Curso Java",
                        ""
                );

        Objective bookObjective =
                manager.addObjective(
                        "Leitura Clean Code",
                        ""
                );

        javaObjective
                .getTimer()
                .start();

        manager.selectObjective(
                bookObjective.getId()
        );

        assertEquals(
                PomodoroStatus.PAUSED,
                javaObjective
                        .getTimer()
                        .snapshot()
                        .status()
        );

        assertSame(
                bookObjective,
                manager.getSelectedObjective()
                        .orElseThrow()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                bookObjective
                        .getTimer()
                        .snapshot()
                        .status()
        );
    }

    @Test
    void shouldPreservePomodoroStateWhenSwitchingObjectives() {

        ObjectiveManager manager =
                new ObjectiveManager();

        Objective javaObjective =
                manager.addObjective(
                        "Curso Java",
                        ""
                );

        Objective dockerObjective =
                manager.addObjective(
                        "Curso Docker",
                        ""
                );

        javaObjective
                .getTimer()
                .start();

        javaObjective
                .getTimer()
                .elapse(
                        Duration.ofSeconds(1)
                );

        manager.selectObjective(
                dockerObjective.getId()
        );

        Duration remainingBeforeReturn =
                javaObjective
                        .getTimer()
                        .snapshot()
                        .remainingTime();

        manager.selectObjective(
                javaObjective.getId()
        );

        assertEquals(
                remainingBeforeReturn,
                javaObjective
                        .getTimer()
                        .snapshot()
                        .remainingTime()
        );

        assertEquals(
                PomodoroStatus.PAUSED,
                javaObjective
                        .getTimer()
                        .snapshot()
                        .status()
        );
    }

    @Test
    void shouldRejectSelectionOfClosedObjective() {

        ObjectiveManager manager =
                new ObjectiveManager();

        Objective javaObjective =
                manager.addObjective(
                        "Curso Java",
                        ""
                );

        javaObjective.complete();

        assertThrows(
                IllegalStateException.class,
                () ->
                        manager.selectObjective(
                                javaObjective.getId()
                        )
        );
    }

    @Test
    void shouldKeepClosedObjectiveInHistoryAndFreeActiveSlot() {

        ObjectiveManager manager =
                new ObjectiveManager();

        Objective firstObjective =
                manager.addObjective(
                        "Curso Java",
                        ""
                );

        manager.addObjective(
                "Docker",
                ""
        );

        manager.addObjective(
                "Linux",
                ""
        );

        manager.addObjective(
                "Redes",
                ""
        );

        manager.addObjective(
                "Kubernetes",
                ""
        );

        firstObjective.complete();

        Objective newObjective =
                manager.addObjective(
                        "Spring Boot",
                        ""
                );

        assertEquals(
                5,
                manager.getActiveObjectiveCount()
        );

        assertEquals(
                6,
                manager.getAllObjectives()
                        .size()
        );

        assertEquals(
                ObjectiveStatus.COMPLETED,
                firstObjective.getStatus()
        );

        assertEquals(
                ObjectiveStatus.ACTIVE,
                newObjective.getStatus()
        );
    }

    @Test
    void shouldCompleteSelectedObjectiveAndSelectNextActiveOne() {

        ObjectiveManager manager =
                new ObjectiveManager();

        Objective javaObjective =
                manager.addObjective(
                        "Curso Java",
                        ""
                );

        Objective dockerObjective =
                manager.addObjective(
                        "Curso Docker",
                        ""
                );

        javaObjective
                .getTimer()
                .start();

        manager.completeObjective(
                javaObjective.getId()
        );

        assertEquals(
                ObjectiveStatus.COMPLETED,
                javaObjective.getStatus()
        );

        assertEquals(
                PomodoroStatus.PAUSED,
                javaObjective
                        .getTimer()
                        .snapshot()
                        .status()
        );

        assertSame(
                dockerObjective,
                manager.getSelectedObjective()
                        .orElseThrow()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                dockerObjective
                        .getTimer()
                        .snapshot()
                        .status()
        );
    }

    @Test
    void shouldMarkObjectiveAsNotFinishedThroughManager() {

        ObjectiveManager manager =
                new ObjectiveManager();

        Objective objective =
                manager.addObjective(
                        "Estudar Kubernetes",
                        ""
                );

        manager.markObjectiveNotFinished(
                objective.getId()
        );

        assertEquals(
                ObjectiveStatus.NOT_FINISHED,
                objective.getStatus()
        );
    }

    @Test
    void shouldDiscardObjectiveThroughManager() {

        ObjectiveManager manager =
                new ObjectiveManager();

        Objective objective =
                manager.addObjective(
                        "Curso antigo",
                        ""
                );

        manager.discardObjective(
                objective.getId()
        );

        assertEquals(
                ObjectiveStatus.DISCARDED,
                objective.getStatus()
        );
    }

    @Test
    void shouldKeepCurrentSelectionWhenClosingAnotherObjective() {

        ObjectiveManager manager =
                new ObjectiveManager();

        Objective javaObjective =
                manager.addObjective(
                        "Curso Java",
                        ""
                );

        Objective dockerObjective =
                manager.addObjective(
                        "Curso Docker",
                        ""
                );

        manager.discardObjective(
                dockerObjective.getId()
        );

        assertSame(
                javaObjective,
                manager.getSelectedObjective()
                        .orElseThrow()
        );

        assertEquals(
                ObjectiveStatus.DISCARDED,
                dockerObjective.getStatus()
        );
    }

    @Test
    void shouldClearSelectionWhenLastActiveObjectiveIsClosed() {

        ObjectiveManager manager =
                new ObjectiveManager();

        Objective objective =
                manager.addObjective(
                        "Curso Java",
                        ""
                );

        manager.completeObjective(
                objective.getId()
        );

        assertTrue(
                manager.getSelectedObjective()
                        .isEmpty()
        );
    }

    @Test
    void shouldRestoreManagerWithActiveAndClosedObjectives() {

        Objective activeObjective =
                new Objective(
                        "Curso Java",
                        ""
                );

        Objective completedObjective =
                new Objective(
                        "Leitura Clean Code",
                        ""
                );

        completedObjective.complete();

        ObjectiveManager manager =
                ObjectiveManager.restore(
                        List.of(
                                completedObjective,
                                activeObjective
                        )
                );

        assertEquals(
                2,
                manager.getAllObjectives()
                        .size()
        );

        assertEquals(
                1,
                manager.getActiveObjectiveCount()
        );

        assertSame(
                activeObjective,
                manager.getSelectedObjective()
                        .orElseThrow()
        );
    }

    @Test
    void shouldReturnClosedObjectivesFromMostRecentToOldest() {

        Objective olderCompleted =
                restoredClosedObjective(
                        "Curso Java",
                        ObjectiveStatus.COMPLETED,
                        Instant.parse(
                                "2026-10-05T18:00:00Z"
                        )
                );

        Objective activeObjective =
                new Objective(
                        "Docker",
                        ""
                );

        Objective newestNotFinished =
                restoredClosedObjective(
                        "Kubernetes",
                        ObjectiveStatus.NOT_FINISHED,
                        Instant.parse(
                                "2026-10-06T18:00:00Z"
                        )
                );

        Objective discardedBetweenThem =
                restoredClosedObjective(
                        "Curso antigo",
                        ObjectiveStatus.DISCARDED,
                        Instant.parse(
                                "2026-10-06T12:00:00Z"
                        )
                );

        ObjectiveManager manager =
                ObjectiveManager.restore(
                        List.of(
                                olderCompleted,
                                activeObjective,
                                newestNotFinished,
                                discardedBetweenThem
                        )
                );

        List<Objective> history =
                manager.getClosedObjectives();

        assertEquals(
                3,
                history.size()
        );

        assertSame(
                newestNotFinished,
                history.get(0)
        );

        assertSame(
                discardedBetweenThem,
                history.get(1)
        );

        assertSame(
                olderCompleted,
                history.get(2)
        );

        assertFalse(
                history.contains(
                        activeObjective
                )
        );
    }

    private static Objective restoredClosedObjective(
            String name,
            ObjectiveStatus status,
            Instant closedAt
    ) {

        return Objective.restore(
                UUID.randomUUID(),
                name,
                "",
                status,
                Instant.parse(
                        "2026-10-01T12:00:00Z"
                ),
                closedAt,
                new PomodoroTimer(
                        new PomodoroSession()
                )
        );
    }
}
