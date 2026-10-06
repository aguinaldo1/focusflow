package io.github.aguinaldo1.focusflow.pomodoro;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class PomodoroClock implements AutoCloseable {

    private static final Duration TICK_INTERVAL =
            Duration.ofSeconds(1);

    private final PomodoroTimer timer;
    private final Runnable intervalCompletedAction;

    private final ScheduledExecutorService scheduler;

    private ScheduledFuture<?> tickTask;

    public PomodoroClock(
            PomodoroTimer timer
    ) {

        this(
                timer,
                () -> {
                }
        );
    }

    public PomodoroClock(
            PomodoroTimer timer,
            Runnable intervalCompletedAction
    ) {

        this.timer =
                Objects.requireNonNull(
                        timer,
                        "timer"
                );

        this.intervalCompletedAction =
                Objects.requireNonNull(
                        intervalCompletedAction,
                        "intervalCompletedAction"
                );

        this.scheduler =
                Executors.newSingleThreadScheduledExecutor(
                        runnable -> {

                            Thread thread =
                                    new Thread(
                                            runnable,
                                            "focusflow-pomodoro-clock"
                                    );

                            thread.setDaemon(true);

                            return thread;
                        }
                );
    }

    public synchronized void start() {

        if (isRunning()) {
            return;
        }

        tickTask =
                scheduler.scheduleAtFixedRate(
                        this::tick,
                        1,
                        1,
                        TimeUnit.SECONDS
                );
    }

    public synchronized void stop() {

        if (tickTask == null) {
            return;
        }

        tickTask.cancel(false);

        tickTask = null;
    }

    public synchronized boolean isRunning() {

        return tickTask != null
                && !tickTask.isCancelled()
                && !tickTask.isDone();
    }

    void tick() {

        boolean intervalCompleted =
                timer.elapse(
                        TICK_INTERVAL
                );

        if (intervalCompleted) {
            intervalCompletedAction.run();
        }
    }

    @Override
    public void close() {

        stop();

        scheduler.shutdownNow();
    }
}
