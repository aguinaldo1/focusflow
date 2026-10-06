package io.github.aguinaldo1.focusflow.objective;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroPhase;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectiveTest {

    @Test
    void shouldCreateActiveObjectiveWithIndependentPomodoro() {

        Objective objective =
                new Objective(
                        "Curso Java Guanabara",
                        "Aprender fundamentos de Java."
                );

        assertNotNull(
                objective.getId()
        );

        assertEquals(
                "Curso Java Guanabara",
                objective.getName()
        );

        assertEquals(
                "Aprender fundamentos de Java.",
                objective.getDescription()
        );

        assertEquals(
                ObjectiveStatus.ACTIVE,
                objective.getStatus()
        );

        assertTrue(
                objective.isActive()
        );

        assertNotNull(
                objective.getCreatedAt()
        );

        assertEquals(
                PomodoroPhase.FOCUS,
                objective.getTimer()
                        .snapshot()
                        .phase()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                objective.getTimer()
                        .snapshot()
                        .status()
        );
    }

    @Test
    void shouldRejectBlankObjectiveName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Objective(
                        "   ",
                        "Descrição"
                )
        );
    }

    @Test
    void shouldAllowEmptyDescription() {

        Objective objective =
                new Objective(
                        "Leitura Clean Code",
                        null
                );

        assertEquals(
                "",
                objective.getDescription()
        );
    }

    @Test
    void shouldCompleteObjectiveAndPauseRunningPomodoro() {

        Objective objective =
                new Objective(
                        "Curso Java",
                        "Estudar Java."
                );

        objective.getTimer()
                .start();

        objective.complete();

        assertEquals(
                ObjectiveStatus.COMPLETED,
                objective.getStatus()
        );

        assertFalse(
                objective.isActive()
        );

        assertNotNull(
                objective.getClosedAt()
        );

        assertEquals(
                PomodoroStatus.PAUSED,
                objective.getTimer()
                        .snapshot()
                        .status()
        );
    }

    @Test
    void shouldMarkObjectiveAsNotFinished() {

        Objective objective =
                new Objective(
                        "Estudar Kubernetes",
                        ""
                );

        objective.markNotFinished();

        assertEquals(
                ObjectiveStatus.NOT_FINISHED,
                objective.getStatus()
        );

        assertNotNull(
                objective.getClosedAt()
        );
    }

    @Test
    void shouldDiscardObjective() {

        Objective objective =
                new Objective(
                        "Curso antigo",
                        ""
                );

        objective.discard();

        assertEquals(
                ObjectiveStatus.DISCARDED,
                objective.getStatus()
        );
    }

    @Test
    void shouldNotAllowClosedObjectiveToBeClosedAgain() {

        Objective objective =
                new Objective(
                        "Curso Java",
                        ""
                );

        objective.complete();

        assertThrows(
                IllegalStateException.class,
                objective::discard
        );
    }
}
