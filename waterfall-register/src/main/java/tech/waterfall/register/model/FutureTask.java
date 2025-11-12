package tech.waterfall.register.model;

import lombok.Data;

import java.time.Instant;

@Data
public class FutureTask extends TaskInfo {
    String cronExpression;
    long fixRateSecs;
    Instant startTime;
    Instant endTime;
    Instant fireTime;
    String timeZoneId;
    long triggerCount;
}
