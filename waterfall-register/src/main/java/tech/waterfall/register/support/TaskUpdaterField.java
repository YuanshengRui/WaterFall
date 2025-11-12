package tech.waterfall.register.support;

public enum TaskUpdaterField {
    cronExpression,
    fixRateSecs,
    fireTime,
    startTime,
    endTime,
    timeZoneId,
    taskRawData,
    unknown;

    public static TaskUpdaterField from(String name) {
        try {
            return TaskUpdaterField.valueOf(name);
        } catch (IllegalArgumentException e) {
            if (name.startsWith("taskRawData")) {
                return taskRawData;
            }
            return unknown;
        }
    }
}
