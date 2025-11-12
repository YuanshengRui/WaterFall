package tech.waterfall.register.dto;

import lombok.Data;

import java.util.List;

@Data
public class IngestResult {
    List<TaskStub> tasks;

    @Data
    public static class TaskStub {
        String taskName;
        String taskId;
        String errorCode;
    }
}
