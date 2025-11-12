package tech.waterfall.register.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ConsumerSettings {
    private String mode;//PUSH or POLL
    //PUSH mode
    private String url;
    private long timeoutSecs;
    private RetrySettings retrySettings;
    //POLL mode
    private int maxNumberOfMessages;
    private int maxCachedMessages;
    private int prefetchTimes;
    private long responseTimeout;
    private long maxCacheTime;
}
