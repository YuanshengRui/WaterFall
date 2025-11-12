package tech.waterfall.register.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;


import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.waterfall.register.dto.Task;
import tech.waterfall.register.dto.TaskUpdater;
import tech.waterfall.register.exception.InvalidTaskException;
import tech.waterfall.register.model.FutureTask;

@Slf4j
public class FutureTaskIngestion {
    private final IFutureTaskService futureTaskService;
    private final ITaskMetaDataManager taskMetaDataManager;

    public FutureTaskIngestion(IFutureTaskService futureTaskService, ITaskMetaDataManager taskMetaDataManager) {
        this.futureTaskService = futureTaskService;
        this.taskMetaDataManager = taskMetaDataManager;
    }

    public Mono<String> ingest(Task task) {
        return Mono.justOrEmpty(task)
                .flatMap(this::validate)
                .map(this::toFutureTask)
                .flatMap(this::doIngest);
    }

    public Mono<Integer> bulkIngest(List<Task> tasks) {
        log.info("begin to ingest future tasks, size: {}", tasks.size());
        return Flux.fromIterable(tasks)
                .flatMap(this::validate)
                .map(this::toFutureTask)
                .doOnNext(futureTaskService::calcFireTime)
                .doOnNext(futureTaskService::trySetTimeInfo)
                .onErrorContinue((e, t) -> log.warn("failed to ingest a future task", e))
                .collectList()
                .flatMapMany(futureTaskService::bulkInsert)
                .collectList()
                .map(List::size)
                .doOnSuccess(size -> log.info("succeeded to ingest {} future tasks", size));
    }

    public Mono<Void> update(String taskName, String taskId, TaskUpdater taskUpdater) {
        return futureTaskService.update(taskName, taskId, taskUpdater.getUpdates());
    }

    public Mono<Void> cancel(String taskName, String taskId) {
        return futureTaskService.cancel(taskName, taskId);
    }

    public Mono<Long> bulkCancel(String taskName, List<String> taskIds) {
        return futureTaskService.cancel(taskName, taskIds);
    }

    public Mono<Task> find(String taskName, String taskId) {
        return futureTaskService.findByTask(taskName, taskId).map(this::toTask);
    }

    private Mono<String> doIngest(FutureTask task) {
        log.trace("begin to ingest a futureTask. {}", task);
        return Mono.justOrEmpty(task)
                .flatMap(t -> futureTaskService.findIdByTask(t.getTaskName(), t.getTaskId()))
                .flatMap(id -> {
                    task.setId(id);
                    return updateFutureTask(task);
                }).switchIfEmpty(Mono.defer(() -> futureTaskService.insert(task).map(id -> task.getTaskId())));
    }

    private Mono<String> updateFutureTask(FutureTask task) {
        log.info("There has been a same future task in framework, update this one(taskName:{}, taskId:{})",
                task.getTaskName(), task.getTaskId());
        return futureTaskService.save(task).map(id -> task.getTaskId());
    }

    private Mono<Task> validate(Task task) {
        if (StringUtils.isEmpty(task.getTaskName()) || StringUtils.isEmpty(task.getTaskId())) {
            return Mono.error(new InvalidTaskException(task,
                    "Invalid future task, taskName and taskId must be present"));
        }
        if (task.getFireTime() == null
                && StringUtils.isEmpty(task.getCronExpression())
                && task.getFixRateSecs() <= 0) {
            return Mono.error(new InvalidTaskException(task,
                    "Invalid future task, one of fireTime, cronExpression and fixRateSecs must be present"));
        }
        return taskMetaDataManager.queueName(task.getTaskName()).thenReturn(task);
    }

    private FutureTask toFutureTask(Task task) {
        FutureTask futureTask = new FutureTask();
        futureTask.setTaskName(task.getTaskName());
        futureTask.setTaskId(task.getTaskId());
        futureTask.setTaskRawData(task.getTaskRawData());
        futureTask.setFireTime(task.getFireTime());
        futureTask.setStartTime(task.getStartTime());
        futureTask.setEndTime(task.getEndTime());
        futureTask.setTimeZoneId(task.getTimeZoneId());
        futureTask.setCronExpression(task.getCronExpression());
        futureTask.setFixRateSecs(task.getFixRateSecs());
        return futureTask;
    }

    private Task toTask(FutureTask futureTask) {
        Task task = new Task();
        task.setTaskName(futureTask.getTaskName());
        task.setTaskId(futureTask.getTaskId());
        task.setTaskRawData(futureTask.getTaskRawData());
        task.setFireTime(futureTask.getFireTime());
        task.setStartTime(futureTask.getStartTime());
        task.setEndTime(futureTask.getEndTime());
        task.setTimeZoneId(futureTask.getTimeZoneId());
        task.setCronExpression(futureTask.getCronExpression());
        task.setFixRateSecs(futureTask.getFixRateSecs());
        return task;
    }
}
