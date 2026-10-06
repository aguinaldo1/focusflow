package io.github.aguinaldo1.focusflow.pomodoro;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PomodoroTimerTest {

    @Test
    void shouldStartWithFullFocusDuration() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        assertEquals(
                Duration.ofMinutes(25),
                timer.getRemainingTime()
        );
    }

    @Test
    void shouldDecreaseRemainingTimeWhileRunning() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(10)
        );

        assertEquals(
                Duration.ofMinutes(15),
                timer.getRemainingTime()
        );
    }

    @Test
    void shouldNotDecreaseTimeWhilePaused() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(5)
        );

        timer.pause();

        timer.elapse(
                Duration.ofMinutes(10)
        );

        assertEquals(
                Duration.ofMinutes(20),
                timer.getRemainingTime()
        );
    }

    @Test
    void shouldMoveToShortBreakWhenFocusTimeEnds() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(25)
        );

        assertEquals(
                PomodoroPhase.SHORT_BREAK,
                session.getPhase()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                session.getStatus()
        );

        assertEquals(
                Duration.ofMinutes(5),
                timer.getRemainingTime()
        );
    }

    @Test
    void shouldIgnoreElapsedTimeWhileIdle() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.elapse(
                Duration.ofMinutes(10)
        );

        assertEquals(
                Duration.ofMinutes(25),
                timer.getRemainingTime()
        );
    }

    @Test
    void shouldRejectInvalidElapsedTime() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        timer.elapse(
                                Duration.ZERO
                        )
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        timer.elapse(
                                Duration.ofSeconds(-1)
                        )
        );
    }

    @Test
    void shouldRestoreFullFocusDurationAfterReset() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(12)
        );

        timer.reset();

        assertEquals(
                PomodoroPhase.FOCUS,
                session.getPhase()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                session.getStatus()
        );

        assertEquals(
                Duration.ofMinutes(25),
                timer.getRemainingTime()
        );
    }

    @Test
    void shouldContinueCountdownAfterResume() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(5)
        );

        timer.pause();

        timer.elapse(
                Duration.ofMinutes(5)
        );

        timer.resume();

        timer.elapse(
                Duration.ofMinutes(5)
        );

        assertEquals(
                Duration.ofMinutes(15),
                timer.getRemainingTime()
        );
    }

    @Test
    void shouldCountdownShortBreakAfterUserStartsIt() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(25)
        );

        assertEquals(
                PomodoroPhase.SHORT_BREAK,
                session.getPhase()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                session.getStatus()
        );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(2)
        );

        assertEquals(
                Duration.ofMinutes(3),
                timer.getRemainingTime()
        );

        assertEquals(
                PomodoroStatus.RUNNING,
                session.getStatus()
        );
    }

    @Test
    void shouldProvideConsistentSnapshot() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(10)
        );

        PomodoroSnapshot snapshot =
                timer.snapshot();

        assertEquals(
                PomodoroPhase.FOCUS,
                snapshot.phase()
        );

        assertEquals(
                PomodoroStatus.RUNNING,
                snapshot.status()
        );

        assertEquals(
                0,
                snapshot.completedFocusCycles()
        );

        assertEquals(
                Duration.ofMinutes(15),
                snapshot.remainingTime()
        );
    }

    @Test
    void shouldRestartCurrentIntervalWithoutLosingCompletedCycles() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(25)
        );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(5)
        );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(10)
        );

        assertEquals(
                1,
                session.getCompletedFocusCycles()
        );

        assertEquals(
                Duration.ofMinutes(15),
                timer.getRemainingTime()
        );

        timer.restartCurrentInterval();

        assertEquals(
                PomodoroPhase.FOCUS,
                session.getPhase()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                session.getStatus()
        );

        assertEquals(
                1,
                session.getCompletedFocusCycles()
        );

        assertEquals(
                Duration.ofMinutes(25),
                timer.getRemainingTime()
        );
    }

    @Test
    void shouldStartBreakAutomaticallyWhenBreakIsReady() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(25)
        );

        assertEquals(
                PomodoroPhase.SHORT_BREAK,
                session.getPhase()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                session.getStatus()
        );

        assertTrue(
                timer.startBreakIfReady()
        );

        assertEquals(
                PomodoroStatus.RUNNING,
                session.getStatus()
        );
    }

    @Test
    void shouldNotAutomaticallyStartNextFocusAfterBreak() {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(
                        session
                );

        timer.start();

        timer.elapse(
                Duration.ofMinutes(25)
        );

        assertTrue(
                timer.startBreakIfReady()
        );

        timer.elapse(
                Duration.ofMinutes(5)
        );

        assertEquals(
                PomodoroPhase.FOCUS,
                session.getPhase()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                session.getStatus()
        );

        assertFalse(
                timer.startBreakIfReady()
        );

        assertEquals(
                PomodoroStatus.IDLE,
                session.getStatus()
        );
    }
}
