package tech.waterfall.register.service;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.waterfall.register.model.GenericTask;
import tech.waterfall.register.support.TaskStatus;

public interface IGenericTaskService {
    Mono<GenericTask> insert(GenericTask task);

    Mono<Void> updateStatus(String id, TaskStatus status);

    Mono<GenericTask> findAndUpdateStatus(String id, TaskStatus oldStatus, TaskStatus newStatus, boolean returnNew);

    Mono<GenericTask> findByTask(String taskName, String taskId);

    Mono<GenericTask> replaceUnqueued(GenericTask genericTask);

    Flux<GenericTask> getAll();

    Mono<String> refreshTask(GenericTask genericTask);

    Flux<GenericTask> bulkInsert(List<GenericTask> tasks);
}
