package tech.waterfall.register.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class Task extends RecurringTask {
    //created from a future task, you should not setup these two fields manually.
    private Instant triggerTime;
    private String parentId; // taskId of the future task that trigger this task
}
