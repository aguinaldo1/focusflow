package io.github.aguinaldo1.focusflow.pomodoro;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PomodoroClockNotificationTest {

    @Test
    void shouldNotifyWhenIntervalFinishes() {

        PomodoroConfig config =
                new PomodoroConfig(
                        Duration.ofSeconds(1),
                        Duration.ofSeconds(1),
                        Duration.ofSeconds(1),
                        4
                );

        PomodoroSession session =
                new PomodoroSession(config);

        PomodoroTimer timer =
                new PomodoroTimer(session);

        AtomicInteger notifications =
                new AtomicInteger();

        try (PomodoroClock clock =
                     new PomodoroClock(
                             timer,
                             notifications::incrementAndGet
                     )) {

            timer.start();

            clock.tick();

            assertEquals(
                    1,
                    notifications.get()
            );

            assertEquals(
                    PomodoroPhase.SHORT_BREAK,
                    timer.snapshot().phase()
            );
        }
    }

    @Test
    void shouldNotNotifyBeforeIntervalFinishes() {

        PomodoroConfig config =
                new PomodoroConfig(
                        Duration.ofSeconds(10),
                        Duration.ofSeconds(5),
                        Duration.ofSeconds(5),
                        4
                );

        PomodoroSession session =
                new PomodoroSession(config);

        PomodoroTimer timer =
                new PomodoroTimer(session);

        AtomicInteger notifications =
                new AtomicInteger();

        try (PomodoroClock clock =
                     new PomodoroClock(
                             timer,
                             notifications::incrementAndGet
                     )) {

            timer.start();

            clock.tick();

            assertEquals(
                    0,
                    notifications.get()
            );
        }
    }
}
