package tech.waterfall.register.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import tech.waterfall.register.dto.IngestResult;
import tech.waterfall.register.dto.Task;
import tech.waterfall.register.dto.TaskUpdater;

@Slf4j
public class TaskIngestion implements ITaskIngestion {

    private final GenericTaskIngestion genericTaskIngestion;
    private final FutureTaskIngestion futureTaskIngestion;

    public TaskIngestion(GenericTaskIngestion genericTaskIngestion, FutureTaskIngestion futureTaskIngestion) {
        this.genericTaskIngestion = genericTaskIngestion;
        this.futureTaskIngestion = futureTaskIngestion;
    }

    @Override
    public Mono<String> ingest(Task task) {
        if (isFutureTask(task)) {
            return ingestFutureTask(task);
        }
        return ingestImmediateTask(task);
    }

    @Override
    public Mono<String> ingestImmediateTask(Task task) {
        return genericTaskIngestion.ingest(task);
    }

    @Override
    public Mono<String> ingestFutureTask(Task task) {
        return futureTaskIngestion.ingest(task);
    }

    @Override
    public Mono<IngestResult> ingestImmediateTasks(List<Task> tasks) {
        return genericTaskIngestion.bulkIngest(tasks);
    }

    @Override
    public Mono<Integer> ingestFutureTasks(List<Task> tasks) {
        return futureTaskIngestion.bulkIngest(tasks);
    }

    @Override
    public Mono<Void> updateFutureTask(String taskName, String taskId, TaskUpdater taskUpdater) {
        return futureTaskIngestion.update(taskName, taskId, taskUpdater);
    }

    @Override
    public Mono<Void> cancelFutureTask(String taskName, String taskId) {
        return futureTaskIngestion.cancel(taskName, taskId);
    }

    @Override
    public Mono<Long> cancelFutureTasks(String taskName, List<String> taskIds) {
        return futureTaskIngestion.bulkCancel(taskName, taskIds);
    }

    @Override
    public Mono<Task> findFutureTask(String taskName, String taskId) {
        return futureTaskIngestion.find(taskName, taskId);
    }

    @Override
    public Mono<Task> findImmediateTask(String taskName, String taskId) {
        return genericTaskIngestion.find(taskName, taskId);
    }

    private boolean isFutureTask(Task task) {
        return StringUtils.isNotEmpty(task.getCronExpression())
                || task.getFixRateSecs() > 0
                || (task.getFireTime() != null);
    }
}
