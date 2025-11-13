package tech.waterfall.register.service;

import java.util.List;

import reactor.core.publisher.Mono;
import tech.waterfall.register.dto.IngestResult;
import tech.waterfall.register.dto.Task;
import tech.waterfall.register.dto.TaskUpdater;

public interface ITaskIngestion {
    Mono<String> ingest(Task task);

    Mono<String> ingestImmediateTask(Task task);

    Mono<String> ingestFutureTask(Task task);

    Mono<IngestResult> ingestImmediateTasks(List<Task> tasks);

    Mono<Integer> ingestFutureTasks(List<Task> tasks);

    Mono<Void> updateFutureTask(String taskName, String taskId, TaskUpdater taskUpdater);

    Mono<Void> cancelFutureTask(String taskName, String taskId);

    Mono<Long> cancelFutureTasks(String taskName, List<String> taskIds);

    Mono<Task> findFutureTask(String taskName, String taskId);

    Mono<Task> findImmediateTask(String taskName, String taskId);
}
