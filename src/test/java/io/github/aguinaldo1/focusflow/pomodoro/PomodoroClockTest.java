package io.github.aguinaldo1.focusflow.pomodoro;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PomodoroClockTest {

    @Test
    void shouldDecreaseTimerByOneSecondPerTick() {

        PomodoroConfig config = new PomodoroConfig(
                Duration.ofSeconds(3),
                Duration.ofSeconds(2),
                Duration.ofSeconds(4),
                4
        );

        PomodoroSession session =
                new PomodoroSession(config);

        PomodoroTimer timer =
                new PomodoroTimer(session);

        try (PomodoroClock clock =
                     new PomodoroClock(timer)) {

            timer.start();

            clock.tick();

            assertEquals(
                    Duration.ofSeconds(2),
                    timer.getRemainingTime()
            );
        }
    }

    @Test
    void shouldNotDecreaseTimerWhilePaused() {

        PomodoroConfig config = new PomodoroConfig(
                Duration.ofSeconds(3),
                Duration.ofSeconds(2),
                Duration.ofSeconds(4),
                4
        );

        PomodoroSession session =
                new PomodoroSession(config);

        PomodoroTimer timer =
                new PomodoroTimer(session);

        try (PomodoroClock clock =
                     new PomodoroClock(timer)) {

            timer.start();

            clock.tick();

            timer.pause();

            clock.tick();

            assertEquals(
                    Duration.ofSeconds(2),
                    timer.getRemainingTime()
            );
        }
    }

    @Test
    void shouldCompleteFocusAfterEnoughTicks() {

        PomodoroConfig config = new PomodoroConfig(
                Duration.ofSeconds(3),
                Duration.ofSeconds(2),
                Duration.ofSeconds(4),
                4
        );

        PomodoroSession session =
                new PomodoroSession(config);

        PomodoroTimer timer =
                new PomodoroTimer(session);

        try (PomodoroClock clock =
                     new PomodoroClock(timer)) {

            timer.start();

            clock.tick();
            clock.tick();
            clock.tick();

            assertEquals(
                    PomodoroPhase.SHORT_BREAK,
                    session.getPhase()
            );

            assertEquals(
                    PomodoroStatus.IDLE,
                    session.getStatus()
            );

            assertEquals(
                    Duration.ofSeconds(2),
                    timer.getRemainingTime()
            );
        }
    }

    @Test
    void shouldStartAndStopRealClock() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(session);

        try (PomodoroClock clock =
                     new PomodoroClock(timer)) {

            assertFalse(clock.isRunning());

            clock.start();

            assertTrue(clock.isRunning());

            clock.stop();

            assertFalse(clock.isRunning());
        }
    }
}
