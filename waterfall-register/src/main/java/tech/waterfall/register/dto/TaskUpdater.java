package tech.waterfall.register.dto;


import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import tech.waterfall.register.support.TaskUpdaterField;

public class TaskUpdater extends HashMap<String, Object> {

    public static Builder builder() {
        TaskUpdater taskUpdater = new TaskUpdater();
        return taskUpdater.new Builder();
    }

    @Override
    public Object put(String key, Object value) {
        TaskUpdaterField updaterField = TaskUpdaterField.from(key);
        switch (updaterField) {
            case endTime:
            case startTime:
            case fireTime:
                return super.put(key, toInstant(key, value));
            case unknown:
                return null;
            default:
                return super.put(key, value);
        }
    }

    private Instant toInstant(String key, Object time) {
        if (time == null) {
            return null;
        }
        if (time instanceof String) {
            String strTime = (String) time;
            if (strTime.isEmpty()) {
                return null;
            }
            return Instant.parse(strTime);
        }
        if (time instanceof Instant) {
            return (Instant) time;
        }
        throw new IllegalArgumentException(String.format("Invalid value type %s for %s",
                key, time.getClass().getName()));
    }

    private boolean isValid(String key) {
        if (TaskUpdaterField.cronExpression.name().equals(key)
                || TaskUpdaterField.fireTime.name().equals(key)
                || TaskUpdaterField.fixRateSecs.name().equals(key)
                || TaskUpdaterField.startTime.name().equals(key)
                || TaskUpdaterField.endTime.name().equals(key)
                || TaskUpdaterField.timeZoneId.name().equals(key)
                || key.startsWith(TaskUpdaterField.taskRawData.name())) {
            return true;
        }
        return false;
    }


    public Map<String, Object> getUpdates() {
        return Collections.unmodifiableMap(this);
    }

    public class Builder {
        public Builder fireTime(Instant fireTime) {
            TaskUpdater.this.put(TaskUpdaterField.fireTime.name(), fireTime);
            return this;
        }

        public Builder cronExpression(String cronExpression) {
            TaskUpdater.this.put(TaskUpdaterField.cronExpression.name(), cronExpression);
            return this;
        }

        public Builder fixRateSecs(long fixRateSecs) {
            TaskUpdater.this.put(TaskUpdaterField.fixRateSecs.name(), fixRateSecs);
            return this;
        }

        public Builder startTime(Instant startTime) {
            TaskUpdater.this.put(TaskUpdaterField.startTime.name(), startTime);
            return this;
        }

        public Builder endTime(Instant endTime) {
            TaskUpdater.this.put(TaskUpdaterField.endTime.name(), endTime);
            return this;
        }

        public Builder timeZoneId(String timeZoneId) {
            TaskUpdater.this.put(TaskUpdaterField.timeZoneId.name(), timeZoneId);
            return this;
        }

        public Builder taskRawData(Map<String, Object> taskRawData) {
            TaskUpdater.this.put(TaskUpdaterField.taskRawData.name(), taskRawData);
            return this;
        }

        public Builder taskRawDataSubField(String subField, Object value) {
            String fullPath = String.join(".", TaskUpdaterField.taskRawData.name(), subField);
            TaskUpdater.this.put(fullPath, value);
            return this;
        }

        public TaskUpdater build() {
            return TaskUpdater.this;
        }
    }
}
