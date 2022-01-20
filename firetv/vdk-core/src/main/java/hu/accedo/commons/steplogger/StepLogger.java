package hu.accedo.commons.steplogger;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import hu.accedo.commons.logging.L;

import static hu.accedo.commons.steplogger.Stopwatch.getNanoString;

/**
 * A tool that can be used for measuring e.g. performance. Display units are automatically chosen based on the value that
 * needs to be displayed (from nanoseconds to days). Use {@link #setEnabled} as a kill switch.
 * <br>
 * The class is thread-safe.
 */
public class StepLogger {
    private static final Object LOCK = new Object();
    private static boolean enabled = true;

    private final Stopwatch stopwatch;
    private final String tag;
    private final AtomicInteger currentStep = new AtomicInteger();
    private final AtomicLong lastStepNanos = new AtomicLong();

    private boolean showStepCount = true;

    /**
     * Creates a StepLogger that is either not started or started.
     *
     * @param tag     the tag to use in LogCat.
     * @param started if true, the stopwatch will start at once, otherwise should be started manually.
     */
    public StepLogger(@NonNull @Size(max = 23) String tag, boolean started) {
        this.tag = tag;
        this.stopwatch = started ? Stopwatch.createStarted() : Stopwatch.createUnstarted();
    }

    /**
     * Starts the stopwatch, if not already running.
     */
    public void start() {
        if (!enabled) {
            return;
        }

        synchronized (LOCK) {
            try {
                stopwatch.start();
            } catch (IllegalArgumentException e) {
                L.w(tag, e);
            }
        }
    }

    /**
     * Stops the stopwatch, if not already stopped. Resets timers.
     */
    public void stop() {
        try {
            synchronized (LOCK) {
                stopwatch.stop();
            }
        } catch (IllegalArgumentException e) {
            L.w(tag, e);
        }
        currentStep.set(0);
        lastStepNanos.set(0);
    }

    /**
     * Prints the step, with the given name.
     *
     * @param stepName the name of the step. When empty, a step-number will be displayed, if enabled.
     */
    public void step(@Nullable String stepName) {
        if (!enabled) {
            return;
        }

        synchronized (LOCK) {
            currentStep.incrementAndGet();
            long elapsed = stopwatch.elapsed(TimeUnit.NANOSECONDS);
            log(stepName, elapsed);
            lastStepNanos.set(elapsed);
        }
    }

    /**
     * Prints a step without name. Only a step-number will be displayed, if enabled.
     */
    public void step() {
        step(null);
    }

    /**
     * Prints the step, with the given name, and stops the timer.
     *
     * @param stepName the name of the step. When empty, a step-number will be displayed, if enabled.
     */
    public void lastStep(String stepName) {
        step(stepName);
        stop();
    }

    /**
     * Prints a step without name, and stops the timer. Only a step-number will be displayed, if enabled.
     */
    public void lastStep() {
        lastStep(null);
    }

    /**
     * @param timeUnit the unit to return the elapsed time in.
     * @return the elapsed time in the given unit.
     */
    public long getElapsed(TimeUnit timeUnit) {
        return stopwatch.elapsed(timeUnit);
    }

    /**
     * @param showStepCount when true, {@StepLogger} will also print the number of the step along with the name of it. Eg: "Step 2 (login)"
     * @return this instance for method chaining.
     */
    public StepLogger setShowStepCount(boolean showStepCount) {
        this.showStepCount = showStepCount;
        return this;
    }

    private void log(String stepName, long elapsed) {
        if (TextUtils.isEmpty(stepName)) {
            if (showStepCount) {
                L.d(tag, String.format(Locale.US, "Step %s: %s (Total %s)", stepName, getNanoString(elapsed - lastStepNanos.get()), getNanoString(elapsed)));
            } else {
                L.d(tag, String.format(Locale.US, "Step %d (%s): %s (Total %s)", currentStep.get(), stepName, getNanoString(elapsed - lastStepNanos.get()), getNanoString(elapsed)));
            }
        } else {
            L.d(tag, String.format(Locale.US, "Step %d: %s (Total %s)", currentStep.get(), getNanoString(elapsed - lastStepNanos.get()), getNanoString(elapsed)));
        }
    }

    /**
     * @param enabled If true, only newly created step loggers will be affected. Those which are already running, won't
     *                start. If false, newly created step loggers will not be able to start anytime. Those which are
     *                already running will support only the stop operation.
     */
    public static void setEnabled(boolean enabled) {
        StepLogger.enabled = enabled;
    }
}
