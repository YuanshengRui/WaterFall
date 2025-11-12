package tech.waterfall.register.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Map;

@Data
public class GenericTask {
    @NotEmpty(message = "Task name must not be empty")
    private String taskName;
    private String taskId;
    private String groupId;
    private Map<String, Object> taskRawData;
    private Map<String, String> taskHeaders;
}
