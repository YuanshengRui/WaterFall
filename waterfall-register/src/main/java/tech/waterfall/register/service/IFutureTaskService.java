package tech.waterfall.register.service;

import java.util.List;
import java.util.Map;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.waterfall.register.model.FutureTask;

public interface IFutureTaskService {
    Mono<String> insert(FutureTask futureTask);

    Mono<String> save(FutureTask futureTask);

    Mono<Void> cancel(String taskName, String taskId);

    Mono<Long> cancel(String taskName, List<String> taskIds);

    Mono<Void> update(String taskName, String taskId, Map taskUpdater);

    Mono<Boolean> hasTask(String taskName, String taskId);

    Flux<FutureTask> getAll();

    Mono<FutureTask> findByTask(String taskName, String taskId);

    Mono<String> findIdByTask(String taskName, String taskId);

    Flux<FutureTask> bulkInsert(List<FutureTask> tasks);

    void calcFireTime(FutureTask futureTask);

    void trySetTimeInfo(FutureTask futureTask);
}
