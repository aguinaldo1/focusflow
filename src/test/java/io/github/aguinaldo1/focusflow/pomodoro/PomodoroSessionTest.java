package io.github.aguinaldo1.focusflow.pomodoro;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PomodoroSessionTest {

    @Test
    void shouldStartInFocusAndIdle() {

        PomodoroSession session = new PomodoroSession();

        assertEquals(PomodoroPhase.FOCUS, session.getPhase());
        assertEquals(PomodoroStatus.IDLE, session.getStatus());
        assertEquals(0, session.getCompletedFocusCycles());
    }

    @Test
    void shouldStartPauseAndResumeFocusSession() {

        PomodoroSession session = new PomodoroSession();

        session.start();

        assertEquals(PomodoroStatus.RUNNING, session.getStatus());

        session.pause();

        assertEquals(PomodoroStatus.PAUSED, session.getStatus());
        assertEquals(PomodoroPhase.FOCUS, session.getPhase());

        session.resume();

        assertEquals(PomodoroStatus.RUNNING, session.getStatus());
    }

    @Test
    void shouldMoveToShortBreakAfterCompletingFocus() {

        PomodoroSession session = new PomodoroSession();

        session.start();
        session.completeCurrentInterval();

        assertEquals(PomodoroPhase.SHORT_BREAK, session.getPhase());
        assertEquals(PomodoroStatus.IDLE, session.getStatus());
        assertEquals(1, session.getCompletedFocusCycles());
    }

    @Test
    void shouldNotPauseAnIdleSession() {

        PomodoroSession session = new PomodoroSession();

        assertThrows(
                IllegalStateException.class,
                session::pause
        );
    }
@Test
void shouldReturnToFocusAfterCompletingShortBreak() {

    PomodoroSession session = new PomodoroSession();

    session.start();
    session.completeCurrentInterval();

    assertEquals(PomodoroPhase.SHORT_BREAK, session.getPhase());

    session.start();
    session.completeCurrentInterval();

    assertEquals(PomodoroPhase.FOCUS, session.getPhase());
    assertEquals(PomodoroStatus.IDLE, session.getStatus());
}

@Test
void shouldMoveToLongBreakAfterFourFocusCycles() {

    PomodoroSession session = new PomodoroSession();

    for (int cycle = 1; cycle <= 4; cycle++) {

        session.start();
        session.completeCurrentInterval();

        if (cycle < 4) {
            session.start();
            session.completeCurrentInterval();
        }
    }

    assertEquals(4, session.getCompletedFocusCycles());
    assertEquals(PomodoroPhase.LONG_BREAK, session.getPhase());
    assertEquals(PomodoroStatus.IDLE, session.getStatus());
 }
}
