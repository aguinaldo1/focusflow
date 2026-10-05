package io.github.aguinaldo1.focusflow.cli;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroClock;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroConfig;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSession;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroTimer;

import java.time.Duration;

public final class FocusFlowCli {

    private FocusFlowCli() {
    }

    public static void main(String[] args) throws InterruptedException {

        PomodoroConfig demoConfig = new PomodoroConfig(
                Duration.ofSeconds(10),
                Duration.ofSeconds(5),
                Duration.ofSeconds(8),
                4
        );

        PomodoroSession session =
                new PomodoroSession(demoConfig);

        PomodoroTimer timer =
                new PomodoroTimer(session);

        System.out.println();
        System.out.println("=================================");
        System.out.println("          FOCUSFLOW");
        System.out.println("=================================");
        System.out.println();
        System.out.println("Modo demonstração");
        System.out.println();
        System.out.println("Foco:        10 segundos");
        System.out.println("Pausa curta:  5 segundos");
        System.out.println("Pausa longa:  8 segundos");
        System.out.println();
        System.out.println("Iniciando foco...");
        System.out.println();

        try (PomodoroClock clock =
                     new PomodoroClock(timer)) {

            timer.start();
            clock.start();

            Duration lastDisplayed = null;

            while (session.getStatus()
                    != PomodoroStatus.IDLE) {

                Duration remaining =
                        timer.getRemainingTime();

                if (!remaining.equals(lastDisplayed)) {

                    System.out.printf(
                            "\rFOCO | %s",
                            formatDuration(remaining)
                    );

                    System.out.flush();

                    lastDisplayed = remaining;
                }

                Thread.sleep(100);
            }

            clock.stop();
        }

        System.out.println();
        System.out.println();
        System.out.println("✓ Foco concluído.");
        System.out.println(
                "Próxima fase: " + session.getPhase()
        );

        System.out.println(
                "Duração: "
                        + formatDuration(
                                timer.getRemainingTime()
                        )
        );

        System.out.println();
        System.out.println(
                "O próximo intervalo permanece parado "
                        + "até o usuário iniciá-lo."
        );

        System.out.println();
    }

    private static String formatDuration(
            Duration duration
    ) {

        long totalSeconds = duration.toSeconds();

        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }
}
