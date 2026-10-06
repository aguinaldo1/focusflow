package io.github.aguinaldo1.focusflow.persistence;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroConfig;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroPhase;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSession;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroTimer;

import java.time.Clock;
import java.time.Duration;
import java.util.Objects;

public final class ObjectiveRestorer {

    private final Clock clock;

    public ObjectiveRestorer() {

        this(
                Clock.systemUTC()
        );
    }

    public ObjectiveRestorer(
            Clock clock
    ) {

        this.clock =
                Objects.requireNonNull(
                        clock,
                        "clock"
                );
    }

    public Objective restore(
            PersistedObjective persisted
    ) {

        Objects.requireNonNull(
                persisted,
                "persisted"
        );

        PomodoroConfig config =
                PomodoroConfig.defaults();

        RecoveredPomodoro recovered =
                recoverPomodoro(
                        persisted,
                        config
                );

        PomodoroSession session =
                PomodoroSession.restore(
                        config,
                        recovered.phase(),
                        recovered.status(),
                        persisted.completedFocusCycles()
                );

        PomodoroTimer timer =
                PomodoroTimer.restore(
                        session,
                        Duration.ofSeconds(
                                recovered.remainingSeconds()
                        )
                );

        return Objective.restore(
                persisted.id(),
                persisted.name(),
                persisted.description(),
                persisted.status(),
                persisted.createdAt(),
                persisted.closedAt(),
                persisted.complexity(),
                persisted.plannedFocusCycles(),
                timer
        );
    }

    private RecoveredPomodoro recoverPomodoro(
            PersistedObjective persisted,
            PomodoroConfig config
    ) {

        /*
         * Objetivos que já não estão ativos não precisam
         * de recuperação baseada no tempo transcorrido.
         */
        if (
                persisted.status()
                        != ObjectiveStatus.ACTIVE
        ) {

            return unchanged(
                    persisted
            );
        }

        /*
         * Somente intervalos que estavam efetivamente
         * rodando precisam de política especial.
         */
        if (
                persisted.pomodoroStatus()
                        != PomodoroStatus.RUNNING
        ) {

            return unchanged(
                    persisted
            );
        }

        /*
         * Foco não avança enquanto o aplicativo está fechado.
         *
         * Ao restaurar, preservamos exatamente o tempo restante,
         * mas voltamos como PAUSED por segurança.
         */
        if (
                persisted.pomodoroPhase()
                        == PomodoroPhase.FOCUS
        ) {

            return new RecoveredPomodoro(
                    PomodoroPhase.FOCUS,
                    PomodoroStatus.PAUSED,
                    persisted.remainingSeconds()
            );
        }

        /*
         * Registros antigos não possuem savedAt.
         *
         * Como não sabemos quanto tempo passou, não podemos
         * descontar tempo da pausa com segurança.
         */
        if (persisted.savedAt() == null) {

            return new RecoveredPomodoro(
                    persisted.pomodoroPhase(),
                    PomodoroStatus.PAUSED,
                    persisted.remainingSeconds()
            );
        }

        long elapsedSeconds =
                Math.max(
                        0L,
                        Duration.between(
                                        persisted.savedAt(),
                                        clock.instant()
                                )
                                .getSeconds()
                );

        long recoveredRemainingSeconds =
                persisted.remainingSeconds()
                        - elapsedSeconds;

        /*
         * A pausa ainda não terminou enquanto o aplicativo
         * esteve fechado.
         *
         * Ela continua automaticamente de onde deveria estar
         * no relógio real.
         */
        if (recoveredRemainingSeconds > 0) {

            return new RecoveredPomodoro(
                    persisted.pomodoroPhase(),
                    PomodoroStatus.RUNNING,
                    recoveredRemainingSeconds
            );
        }

        /*
         * A pausa terminou enquanto o aplicativo estava fechado.
         *
         * Não iniciamos outro ciclo de foco automaticamente.
         * Apenas deixamos o próximo foco pronto para o usuário.
         */
        return new RecoveredPomodoro(
                PomodoroPhase.FOCUS,
                PomodoroStatus.IDLE,
                config
                        .durationFor(
                                PomodoroPhase.FOCUS
                        )
                        .toSeconds()
        );
    }

    private static RecoveredPomodoro unchanged(
            PersistedObjective persisted
    ) {

        return new RecoveredPomodoro(
                persisted.pomodoroPhase(),
                persisted.pomodoroStatus(),
                persisted.remainingSeconds()
        );
    }

    private record RecoveredPomodoro(
            PomodoroPhase phase,
            PomodoroStatus status,
            long remainingSeconds
    ) {
    }
}
