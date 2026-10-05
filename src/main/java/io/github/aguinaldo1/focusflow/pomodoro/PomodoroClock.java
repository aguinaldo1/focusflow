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
    private final ScheduledExecutorService scheduler;

    private ScheduledFuture<?> tickTask;

    public PomodoroClock(PomodoroTimer timer) {

        this.timer = Objects.requireNonNull(
                timer,
                "Pomodoro timer cannot be null."
        );

        this.scheduler =
                Executors.newSingleThreadScheduledExecutor(
                        runnable -> {

                            Thread thread = new Thread(
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

        tickTask = scheduler.scheduleAtFixedRate(
                this::tick,
                TICK_INTERVAL.toSeconds(),
                TICK_INTERVAL.toSeconds(),
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
        timer.elapse(TICK_INTERVAL);
    }

    @Override
    public void close() {

        stop();

        scheduler.shutdownNow();
    }
}
