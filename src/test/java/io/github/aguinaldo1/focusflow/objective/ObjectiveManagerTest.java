package io.github.aguinaldo1.focusflow.objective;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                () -> manager.addObjective(
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
                () -> manager.selectObjective(
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
}
