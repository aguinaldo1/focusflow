package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroPhase;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SqliteObjectiveRepositoryTest {

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
    void shouldSaveAndFindActiveObjective()
            throws Exception {

        Objective objective =
                new Objective(
                        "Curso Java Guanabara",
                        "Aprender fundamentos de Java."
                );

        repository.save(
                objective
        );

        List<PersistedObjective> objectives =
                repository.findAll();

        assertEquals(
                1,
                objectives.size()
        );

        PersistedObjective persisted =
                objectives.getFirst();

        assertEquals(
                objective.getId(),
                persisted.id()
        );

        assertEquals(
                "Curso Java Guanabara",
                persisted.name()
        );

        assertEquals(
                "Aprender fundamentos de Java.",
                persisted.description()
        );

        assertEquals(
                ObjectiveStatus.ACTIVE,
                persisted.status()
        );

        assertEquals(
                PomodoroPhase.FOCUS,
                persisted.pomodoroPhase()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                persisted.pomodoroStatus()
        );

        assertEquals(
                0,
                persisted.completedFocusCycles()
        );

        assertEquals(
                1500,
                persisted.remainingSeconds()
        );
    }

    @Test
    void shouldPersistPomodoroProgress()
            throws Exception {

        Objective objective =
                new Objective(
                        "Leitura Clean Code",
                        ""
                );

        objective
                .getTimer()
                .start();

        objective
                .getTimer()
                .elapse(
                        Duration.ofSeconds(60)
                );

        objective
                .getTimer()
                .pause();

        repository.save(
                objective
        );

        PersistedObjective persisted =
                repository
                        .findAll()
                        .getFirst();

        assertEquals(
                PomodoroStatus.PAUSED,
                persisted.pomodoroStatus()
        );

        assertEquals(
                1440,
                persisted.remainingSeconds()
        );
    }

    @Test
    void shouldUpdateExistingObjectiveInsteadOfDuplicatingIt()
            throws Exception {

        Objective objective =
                new Objective(
                        "Curso Java",
                        ""
                );

        repository.save(
                objective
        );

        objective.complete();

        repository.save(
                objective
        );

        List<PersistedObjective> objectives =
                repository.findAll();

        assertEquals(
                1,
                objectives.size()
        );

        PersistedObjective persisted =
                objectives.getFirst();

        assertEquals(
                ObjectiveStatus.COMPLETED,
                persisted.status()
        );

        assertNotNull(
                persisted.closedAt()
        );
    }
	@Test
void shouldRestoreObjectiveAfterReadingFromDatabase()
        throws Exception {

    Objective original =
            new Objective(
                    "Curso Java Guanabara",
                    "Aprender Java."
            );

    original
            .getTimer()
            .start();

    original
            .getTimer()
            .elapse(
                    Duration.ofSeconds(120)
            );

    original
            .getTimer()
            .pause();

    repository.save(
            original
    );

    PersistedObjective persisted =
            repository
                    .findAll()
                    .getFirst();

    Objective restored =
            new ObjectiveRestorer()
                    .restore(
                            persisted
                    );

    assertEquals(
            original.getId(),
            restored.getId()
    );

    assertEquals(
            original.getName(),
            restored.getName()
    );

    assertEquals(
            1380,
            restored
                    .getTimer()
                    .snapshot()
                    .remainingTime()
                    .toSeconds()
    );

    assertEquals(
            PomodoroStatus.PAUSED,
            restored
                    .getTimer()
                    .snapshot()
                    .status()
    );
}
@Test
void shouldPersistComplexityAndAdjustedPlannedCycles()
        throws Exception {

    Objective objective =
            new Objective(
                    "Kubernetes avançado",
                    "",
                    io.github.aguinaldo1.focusflow.objective.ObjectiveComplexity.HARD
            );

    objective.increasePlannedFocusCycles();

    repository.save(
            objective
    );

    PersistedObjective persisted =
            repository
                    .findAll()
                    .get(0);

    assertEquals(
            io.github.aguinaldo1.focusflow.objective.ObjectiveComplexity.HARD,
            persisted.complexity()
    );

    assertEquals(
            7,
            persisted.plannedFocusCycles()
    );
}

}
