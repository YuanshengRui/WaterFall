package tech.waterfall.register.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ResponseTask extends GenericTask {
    private Instant createdTime;
    private Instant triggerTime;
}
