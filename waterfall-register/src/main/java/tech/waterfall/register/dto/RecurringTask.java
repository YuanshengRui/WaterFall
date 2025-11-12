package tech.waterfall.register.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class RecurringTask extends GenericTask {
    private String cronExpression;
    private long fixRateSecs;
    private Instant fireTime;
    private Instant startTime;
    private Instant endTime;
    private String timeZoneId;
}
