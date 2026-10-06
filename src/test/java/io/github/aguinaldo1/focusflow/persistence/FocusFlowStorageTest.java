package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveManager;
import io.github.aguinaldo1.focusflow.objective.ObjectiveStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FocusFlowStorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldPreserveObjectivesBetweenExecutions()
            throws Exception {

        Path databasePath =
                temporaryDirectory
                        .resolve("focusflow-data")
                        .resolve("focusflow.db");

        FocusFlowStorage firstExecution =
                new FocusFlowStorage(
                        databasePath
                );

        ObjectiveManager originalManager =
                new ObjectiveManager();

        Objective javaObjective =
                originalManager.addObjective(
                        "Curso Java Guanabara",
                        "Continuar estudos de Java."
                );

        javaObjective
                .getTimer()
                .start();

        javaObjective
                .getTimer()
                .elapse(
                        Duration.ofSeconds(120)
                );

        Objective bookObjective =
                originalManager.addObjective(
                        "Leitura Clean Code",
                        ""
                );

        bookObjective.complete();

        firstExecution.save(
                originalManager
        );

        assertTrue(
                Files.exists(
                        databasePath
                )
        );

        FocusFlowStorage secondExecution =
                new FocusFlowStorage(
                        databasePath
                );

        ObjectiveManager restoredManager =
                secondExecution.load();

        assertEquals(
                2,
                restoredManager
                        .getAllObjectives()
                        .size()
        );

        assertEquals(
                1,
                restoredManager
                        .getActiveObjectiveCount()
        );

        Objective restoredJava =
                restoredManager
                        .getSelectedObjective()
                        .orElseThrow();

        assertEquals(
                javaObjective.getId(),
                restoredJava.getId()
        );

        assertEquals(
                PomodoroStatus.PAUSED,
                restoredJava
                        .getTimer()
                        .snapshot()
                        .status()
        );

        assertEquals(
                1380,
                restoredJava
                        .getTimer()
                        .snapshot()
                        .remainingTime()
                        .toSeconds()
        );

        assertTrue(
                restoredManager
                        .getAllObjectives()
                        .stream()
                        .anyMatch(
                                objective ->
                                        objective.getStatus()
                                                == ObjectiveStatus.COMPLETED
                        )
        );
    }
}
