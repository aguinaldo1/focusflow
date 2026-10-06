package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveManager;
import io.github.aguinaldo1.focusflow.objective.ObjectiveStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectiveLoaderTest {

    @TempDir
    Path temporaryDirectory;

    private SqliteObjectiveRepository repository;

    @BeforeEach
    void setUp()
            throws Exception {

        Path databasePath =
                temporaryDirectory.resolve(
                        "focusflow-test.db"
                );

        DatabaseManager databaseManager =
                new DatabaseManager(
                        databasePath
                );

        databaseManager.initialize();

        repository =
                new SqliteObjectiveRepository(
                        databaseManager
                );
    }

    @Test
    void shouldLoadActiveAndHistoricalObjectives()
            throws Exception {

        Objective activeObjective =
                new Objective(
                        "Curso Java Guanabara",
                        "Continuar estudos de Java."
                );

        activeObjective
                .getTimer()
                .start();

        activeObjective
                .getTimer()
                .elapse(
                        Duration.ofSeconds(120)
                );

        Objective completedObjective =
                new Objective(
                        "Leitura Clean Code",
                        ""
                );

        completedObjective.complete();

        repository.save(
                activeObjective
        );

        repository.save(
                completedObjective
        );

        ObjectiveLoader loader =
                new ObjectiveLoader(
                        repository,
                        new ObjectiveRestorer()
                );

        ObjectiveManager restoredManager =
                loader.load();

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

        Objective restoredActive =
                restoredManager
                        .getSelectedObjective()
                        .orElseThrow();

        assertEquals(
                activeObjective.getId(),
                restoredActive.getId()
        );

        assertEquals(
                PomodoroStatus.PAUSED,
                restoredActive
                        .getTimer()
                        .snapshot()
                        .status()
        );

        assertEquals(
                1380,
                restoredActive
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

    @Test
    void shouldLoadEmptyManagerWhenDatabaseHasNoObjectives()
            throws Exception {

        ObjectiveLoader loader =
                new ObjectiveLoader(
                        repository,
                        new ObjectiveRestorer()
                );

        ObjectiveManager manager =
                loader.load();

        assertTrue(
                manager
                        .getAllObjectives()
                        .isEmpty()
        );

        assertTrue(
                manager
                        .getSelectedObjective()
                        .isEmpty()
        );
    }
}

