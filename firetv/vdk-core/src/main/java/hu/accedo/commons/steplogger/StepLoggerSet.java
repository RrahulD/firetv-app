package hu.accedo.commons.steplogger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A helper class for StepLogger to easily measure different concurrent tasks.
 */
public class StepLoggerSet {
    private ConcurrentHashMap<String, StepLogger> stepLoggers = new ConcurrentHashMap<>();
    private String tag;

    public StepLoggerSet(@NonNull @Size(max = 23) String tag) {
        this.tag = tag;
    }

    /**
     * Creates and starts the timer of a new task.
     *
     * @param taskName      the task to create.
     * @param showStepCount when true, the logger will count the number of steps taken so far.
     */
    public void startTask(@NonNull String taskName, boolean showStepCount) {
        if (!stepLoggers.contains(taskName)) {
            stepLoggers.put(taskName, new StepLogger(tag, true).setShowStepCount(showStepCount));
        }
    }

    /**
     * Steps the given task, with a specific message.
     *
     * @param taskName the task to step.
     * @param stepName the name of the step inside the task.
     */
    public void stepTask(@NonNull String taskName, @Nullable String stepName) {
        StepLogger logger = stepLoggers.get(taskName);
        if (logger != null) {
            logger.step(stepName == null ? taskName : taskName + " " + stepName);
        }
    }

    /**
     * Steps the given task with no message.
     *
     * @param taskName the task to step.
     */
    public void stepTask(@NonNull String taskName) {
        stepTask(taskName, null);
    }

    /**
     * Steps and stops the given task.
     *
     * @param taskName the task to step and stop.
     * @param stepName the name of the step inside the task.
     */
    public void lastStepTask(@NonNull String taskName, @Nullable String stepName) {
        stepTask(taskName, stepName);
        stopTask(taskName);
    }

    /**
     * Steps and stops the given task with no message.
     *
     * @param taskName the task to step and stop.
     */
    public void lastStepTask(@NonNull String taskName) {
        lastStepTask(taskName, null);
    }

    /**
     * Stops the given task.
     *
     * @param taskName the task to stop.
     */
    public void stopTask(@NonNull String taskName) {
        StepLogger logger = stepLoggers.remove(taskName);
        if (logger != null) {
            logger.stop();
        }
    }

    /**
     * Stops all tasks.
     */
    public void stopAll() {
        for (StepLogger stepLogger : stepLoggers.values()) {
            stepLogger.stop();
        }
        stepLoggers.clear();
    }
}
