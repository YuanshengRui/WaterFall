package tech.waterfall.register.model;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class TaskInfo {
    private String id;
    private String taskName;
    private String taskId;
    private String groupId;
    private Map<String, Object> taskRawData;
    private Map<String, String> taskHeaders;
    private Instant lastModifiedTime;
    private Instant createdTime;
}
