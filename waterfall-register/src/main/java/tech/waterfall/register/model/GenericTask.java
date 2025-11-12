package tech.waterfall.register.model;

import lombok.Data;
import tech.waterfall.register.support.TaskStatus;

import java.time.Instant;

@Data
public class GenericTask extends TaskInfo {
    TaskStatus taskStatus;
    Instant triggerTime;
    String parentId;
    int retries;
}
