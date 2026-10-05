package io.github.aguinaldo1.focusflow.cli;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroClock;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroConfig;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSession;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSnapshot;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroTimer;

import java.time.Duration;
import java.util.Scanner;

public final class FocusFlowCli {

    private FocusFlowCli() {
    }

    public static void main(String[] args) {

        PomodoroConfig demoConfig = new PomodoroConfig(
                Duration.ofSeconds(30),
                Duration.ofSeconds(10),
                Duration.ofSeconds(15),
                4
        );

        PomodoroSession session =
                new PomodoroSession(demoConfig);

        PomodoroTimer timer =
                new PomodoroTimer(session);

        printHeader();

        try (
                PomodoroClock clock =
                        new PomodoroClock(timer);

                Scanner scanner =
                        new Scanner(System.in)
        ) {

            clock.start();

            printHelp();
            printStatus(timer.snapshot());

            boolean applicationRunning = true;

            while (applicationRunning) {

                System.out.print("\nfocusflow> ");

                if (!scanner.hasNextLine()) {
                    break;
                }

                String command =
                        scanner.nextLine()
                                .trim()
                                .toLowerCase();

                try {

                    switch (command) {

                        case "start" -> {

                            timer.start();

                            System.out.println(
                                    "Intervalo iniciado."
                            );

                            printStatus(
                                    timer.snapshot()
                            );
                        }

                        case "pause" -> {

                            timer.pause();

                            System.out.println(
                                    "Intervalo pausado."
                            );

                            printStatus(
                                    timer.snapshot()
                            );
                        }

                        case "resume" -> {

                            timer.resume();

                            System.out.println(
                                    "Intervalo retomado."
                            );

                            printStatus(
                                    timer.snapshot()
                            );
                        }

                        case "reset" -> {

                            timer.reset();

                            System.out.println(
                                    "Pomodoro reiniciado."
                            );

                            printStatus(
                                    timer.snapshot()
                            );
                        }

                        case "status" ->
                                printStatus(
                                        timer.snapshot()
                                );

                        case "help" ->
                                printHelp();

                        case "exit" -> {

                            applicationRunning = false;

                            System.out.println(
                                    "Encerrando FocusFlow..."
                            );
                        }

                        case "" -> {
                        }

                        default ->
                                System.out.println(
                                        "Comando desconhecido. "
                                                + "Digite 'help'."
                                );
                    }

                } catch (IllegalStateException exception) {

                    System.out.println(
                            "Operação não permitida: "
                                    + exception.getMessage()
                    );
                }
            }
        }

        System.out.println("FocusFlow encerrado.");
    }

    private static void printHeader() {

        System.out.println();
        System.out.println(
                "================================="
        );

        System.out.println(
                "          FOCUSFLOW"
        );

        System.out.println(
                "================================="
        );

        System.out.println();
        System.out.println(
                "Modo interativo de demonstração"
        );

        System.out.println();
        System.out.println(
                "Foco:        30 segundos"
        );

        System.out.println(
                "Pausa curta: 10 segundos"
        );

        System.out.println(
                "Pausa longa: 15 segundos"
        );
    }

    private static void printHelp() {

        System.out.println();
        System.out.println("Comandos:");

        System.out.println(
                "  start  - iniciar intervalo"
        );

        System.out.println(
                "  pause  - pausar intervalo"
        );

        System.out.println(
                "  resume - continuar intervalo"
        );

        System.out.println(
                "  reset  - reiniciar Pomodoro"
        );

        System.out.println(
                "  status - mostrar estado atual"
        );

        System.out.println(
                "  help   - mostrar comandos"
        );

        System.out.println(
                "  exit   - sair"
        );
    }

    private static void printStatus(
            PomodoroSnapshot snapshot
    ) {

        System.out.println();
        System.out.println(
                "--------- STATUS ---------"
        );

        System.out.println(
                "Fase:       "
                        + snapshot.phase()
        );

        System.out.println(
                "Estado:     "
                        + snapshot.status()
        );

        System.out.println(
                "Restante:   "
                        + formatDuration(
                                snapshot.remainingTime()
                        )
        );

        System.out.println(
                "Ciclos:     "
                        + snapshot.completedFocusCycles()
        );

        System.out.println(
                "--------------------------"
        );
    }

    private static String formatDuration(
            Duration duration
    ) {

        long totalSeconds =
                duration.toSeconds();

        long minutes =
                totalSeconds / 60;

        long seconds =
                totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }
}
