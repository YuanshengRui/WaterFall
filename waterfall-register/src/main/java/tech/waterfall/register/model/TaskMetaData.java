package tech.waterfall.register.model;

import lombok.Data;

@Data
public class TaskMetaData implements IIdentity<String> {
    String id;
    String taskCategory;
    String taskName;
    String queueName;
    ConsumerSettings consumerSettings;
    RecoverySettings recoverySettings;
}
