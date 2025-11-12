package tech.waterfall.register.model;

import lombok.Data;
import tech.waterfall.register.support.RetryStrategy;

@Data
public class RetrySettings {
    int retries;
    RetryStrategy strategy;
    long minBackoffSecs;
    long maxBackoffSecs;
    double jitter;
}
